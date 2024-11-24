package synapps.resona.api.oauth.filter;


import synapps.resona.api.global.utils.HeaderUtil;
import synapps.resona.api.oauth.token.AuthToken;
import synapps.resona.api.oauth.token.AuthTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        log.debug("TokenAuthenticationFilter started for URI: {}", request.getRequestURI());

        String tokenStr = HeaderUtil.getAccessToken(request);
        log.debug("Received token: {}", tokenStr);

        if (StringUtils.hasText(tokenStr)) {
            try {
                log.debug("Attempting to convert and validate token");
                AuthToken token = tokenProvider.convertAuthToken(tokenStr);

                log.debug("Token converted, validating...");
                if (token.validate()) {
                    log.debug("Token is valid, getting authentication");
                    Authentication authentication = tokenProvider.getAuthentication(token);
                    log.debug("Authentication object created: {}", authentication);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Set Authentication to security context for '{}', uri: {}", authentication.getName(), request.getRequestURI());

                    // 현재 SecurityContext의 상태 로깅
                    log.debug("Current SecurityContext: {}", SecurityContextHolder.getContext());
                } else {
                    log.warn("Invalid token, uri: {}", request.getRequestURI());
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid token");
                    return;
                }
            } catch (Exception e) {
                log.error("Could not set user authentication in security context", e);
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token processing error: " + e.getMessage());
                return;
            }
        } else {
            log.warn("No token found in request headers, uri: {}", request.getRequestURI());
            SecurityContextHolder.clearContext();
        }

        log.debug("TokenAuthenticationFilter completed for URI: {}", request.getRequestURI());
        log.debug("Final SecurityContext state: {}", SecurityContextHolder.getContext());
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.debug("Checking if should not filter for path: {}", path);
        boolean shouldNotFilter = path.startsWith("/public") || path.equals("/error");
        log.debug("Should not filter: {}", shouldNotFilter);
        return shouldNotFilter;
    }
}
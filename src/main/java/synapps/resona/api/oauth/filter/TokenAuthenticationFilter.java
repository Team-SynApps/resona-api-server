package synapps.resona.api.oauth.filter;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synapps.resona.api.global.utils.HeaderUtil;
import synapps.resona.api.oauth.token.AuthToken;
import synapps.resona.api.oauth.token.AuthTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        final Logger logger = LogManager.getLogger(TokenAuthenticationFilter.class);
        logger.debug("TokenAuthenticationFilter started for URI: {}", request.getRequestURI());

        String tokenStr = HeaderUtil.getAccessToken(request);
        logger.debug("Received token: {}", tokenStr);

        if (StringUtils.hasText(tokenStr)) {
            try {
                logger.debug("Attempting to convert and validate token");
                AuthToken token = tokenProvider.convertAuthToken(tokenStr);

                logger.debug("Token converted, validating...");
                if (token.validate()) {
                    logger.debug("Token is valid, getting authentication");
                    Authentication authentication = tokenProvider.getAuthentication(token);
                    logger.debug("Authentication object created: {}", authentication);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Set Authentication to security context for '{}', uri: {}", authentication.getName(), request.getRequestURI());

                    // 현재 SecurityContext의 상태 로깅
                    logger.debug("Current SecurityContext: {}", SecurityContextHolder.getContext());
                } else {
                    logger.warn("Invalid token, uri: {}", request.getRequestURI());
                    SecurityContextHolder.clearContext();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid token");
                    return;
                }
            } catch (Exception e) {
                logger.error("Could not set user authentication in security context", e);
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token processing error: " + e.getMessage());
                return;
            }
        } else {
            logger.warn("No token found in request headers, uri: {}", request.getRequestURI());
            SecurityContextHolder.clearContext();
        }

        logger.debug("TokenAuthenticationFilter completed for URI: {}", request.getRequestURI());
        logger.debug("Final SecurityContext state: {}", SecurityContextHolder.getContext());
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final Logger logger = LogManager.getLogger(TokenAuthenticationFilter.class);
        String path = request.getRequestURI();
        logger.debug("Checking if should not filter for path: {}", path);
        boolean shouldNotFilter = path.startsWith("/public") || path.equals("/error");
        logger.debug("Should not filter: {}", shouldNotFilter);
        return shouldNotFilter;
    }
}
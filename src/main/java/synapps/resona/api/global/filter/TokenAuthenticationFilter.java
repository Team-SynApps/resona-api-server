package synapps.resona.api.global.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.ErrorMetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.global.exception.ErrorCode;
import synapps.resona.api.global.utils.HeaderUtil;
import synapps.resona.api.mysql.token.AuthToken;
import synapps.resona.api.mysql.token.AuthTokenProvider;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LogManager.getLogger(TokenAuthenticationFilter.class);

    private final AuthTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;
    private final ServerInfoConfig serverInfo;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String tokenStr = HeaderUtil.getAccessToken(request);
        logger.debug("Received token: {}", tokenStr);

        if (StringUtils.hasText(tokenStr)) {
            try {
                AuthToken token = tokenProvider.convertAuthToken(tokenStr);

                if (token.validate()) {
                    Authentication authentication = tokenProvider.getAuthentication(token);
                    logger.debug("Token authorities before setting context: {}",
                            authentication.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Set Authentication to security context for '{}', uri: {}, authorities: {}",
                            authentication.getName(),
                            request.getRequestURI(),
                            authentication.getAuthorities());
                } else {
                    logger.warn("Invalid token, uri: {}", request.getRequestURI());
                    SecurityContextHolder.clearContext();
                    handleAuthenticationError(request, response, ErrorCode.INVALID_TOKEN);
                    return;
                }
            } catch (ExpiredJwtException e) {
                logger.error("Token expired", e);
                SecurityContextHolder.clearContext();
                handleAuthenticationError(request, response, ErrorCode.EXPIRED_TOKEN);
                return;
            } catch (Exception e) {
                logger.error("Could not set user authentication in security context", e);
                SecurityContextHolder.clearContext();
                handleAuthenticationError(request, response, ErrorCode.INVALID_TOKEN);
                return;
            }
        } else {
            logger.warn("No token found in request headers, uri: {}", request.getRequestURI());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void handleAuthenticationError(
            HttpServletRequest request,
            HttpServletResponse response,
            ErrorCode errorCode) throws IOException {

        ErrorMetaDataDto metaData = ErrorMetaDataDto.createErrorMetaData(
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                request.getRequestURI(),
                serverInfo.getVersionNumber(),
                serverInfo.getServerName(),
                errorCode.getCode()
        );

        ResponseDto responseData = new ResponseDto(
                metaData,
                List.of(Map.of("error", errorCode.getMessage()))
        );

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(responseData);
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        logger.debug("Checking if should not filter for path: {}", path);
        boolean shouldNotFilter = path.startsWith("/public") || path.equals("/error");
        logger.debug("Should not filter: {}", shouldNotFilter);
        return shouldNotFilter;
    }
}
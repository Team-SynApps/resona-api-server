package synapps.resona.api.mysql.member.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import synapps.resona.api.oauth.token.AuthToken;
import synapps.resona.api.oauth.token.AuthTokenProvider;

@Component("memberSecurity")
public class MemberSecurity {
    private final AuthTokenProvider authTokenProvider;
    private static final Logger log = LogManager.getLogger(MemberSecurity.class);

    public MemberSecurity(AuthTokenProvider authTokenProvider) {
        log.debug("MemberSecurity bean created with authTokenProvider: {}", authTokenProvider);
        this.authTokenProvider = authTokenProvider;
    }

    public boolean isCurrentUser(HttpServletRequest request) {
        try {
            String token = resolveToken(request);
            log.debug("Resolved token: {}", token);

            if (token == null) {
                log.debug("Token is null");
                return false;
            }

            AuthToken authToken = authTokenProvider.convertAuthToken(token);
            if (!authToken.validate()) {
                log.debug("Token validation failed");
                return false;
            }

            Authentication authentication = authTokenProvider.getAuthentication(authToken);
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

            log.debug("Token Authentication: {}", authentication);
            log.debug("Current Authentication: {}", currentAuth);

            if (authentication == null || currentAuth == null) {
                log.debug("Either authentication or currentAuth is null");
                return false;
            }

            boolean result = authentication.isAuthenticated() &&
                    authentication.getName().equals(currentAuth.getName());

            log.debug("isCurrentUser result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error in isCurrentUser", e);
            return false;
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

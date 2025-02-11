package synapps.resona.api.oauth.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    @Value("${oauth.redirect-scheme}")
    private String redirectScheme;

    public CustomOAuth2AuthorizationRequestResolver(OAuth2AuthorizationRequestResolver defaultResolver) {
        this.defaultResolver = defaultResolver;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        if (request.getRequestURI().endsWith("/apple")) {
            return null;
        }
        saveRedirectScheme(request);
        return defaultResolver.resolve(request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        if (request.getRequestURI().endsWith("/apple")) {
            return null;
        }
        saveRedirectScheme(request);
        return defaultResolver.resolve(request, clientRegistrationId);
    }

    private void saveRedirectScheme(HttpServletRequest request) {
        String redirectUri = request.getParameter(redirectScheme);
        if (redirectUri != null) {
            HttpSession session = request.getSession();
            session.setAttribute(redirectScheme, redirectUri);
        }
    }
}

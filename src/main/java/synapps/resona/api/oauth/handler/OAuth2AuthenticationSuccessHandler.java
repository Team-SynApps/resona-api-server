package synapps.resona.api.oauth.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import synapps.resona.api.global.dto.MetaDataDto;
import synapps.resona.api.global.dto.ResponseDto;
import synapps.resona.api.global.properties.AppProperties;
import synapps.resona.api.global.utils.CookieUtil;
import synapps.resona.api.mysql.member.entity.member.MemberRefreshToken;
import synapps.resona.api.mysql.member.repository.MemberRefreshTokenRepository;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.RoleType;
import synapps.resona.api.oauth.info.OAuth2UserInfo;
import synapps.resona.api.oauth.info.OAuth2UserInfoFactory;
import synapps.resona.api.oauth.respository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import synapps.resona.api.oauth.token.AuthToken;
import synapps.resona.api.oauth.token.AuthTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthTokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final MemberRefreshTokenRepository memberRefreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        ResponseEntity<?> responseData = getResponse(request, response, authentication);

//        if (response.isCommitted()) {
//            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
//            return;
//        }

        clearAuthenticationAttributes(request, response);
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
        writeResponse(response, responseData);
    }

    protected ResponseEntity<?> getResponse(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        ProviderType providerType = ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());

        OidcUser user = ((OidcUser) authentication.getPrincipal());
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        Collection<? extends GrantedAuthority> authorities = ((OidcUser) authentication.getPrincipal()).getAuthorities();

        RoleType roleType = hasAuthority(authorities, RoleType.ADMIN.getCode()) ? RoleType.ADMIN : RoleType.USER;

        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                userInfo.getEmail(),
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        // refresh 토큰 설정
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // DB 저장
        MemberRefreshToken userRefreshToken = memberRefreshTokenRepository.findByMemberEmail(userInfo.getId());
        if (userRefreshToken != null) {
            userRefreshToken.setRefreshToken(refreshToken.getToken());
        } else {
            userRefreshToken = new MemberRefreshToken(userInfo.getEmail(), refreshToken.getToken());
            memberRefreshTokenRepository.saveAndFlush(userRefreshToken);
        }

        int cookieMaxAge = (int) refreshTokenExpiry / 60;

        CookieUtil.deleteCookie(request, response, OAuth2AuthorizationRequestBasedOnCookieRepository.REFRESH_TOKEN);
        CookieUtil.addCookie(response, OAuth2AuthorizationRequestBasedOnCookieRepository.REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        MetaDataDto metaData = MetaDataDto.createSuccessMetaData(request.getQueryString(), "1","api server");
        ResponseDto responseData = new ResponseDto(metaData, List.of(accessToken.getToken()));

        return ResponseEntity.ok(responseData);
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private void writeResponse(HttpServletResponse response, ResponseEntity<?> responseData) throws IOException {
        // 상태 코드 설정
        response.setStatus(responseData.getStatusCodeValue());

        // Content-Type 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Body를 HttpServletResponse로 작성
        if (responseData.getBody() != null) {
            String responseBody = new ObjectMapper().writeValueAsString(responseData.getBody());
            response.getWriter().write(responseBody);
        }
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
        if (authorities == null) {
            return false;
        }

        for (GrantedAuthority grantedAuthority : authorities) {
            if (authority.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want to
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    System.out.println("authorizedURI: " + authorizedURI);
                    if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}


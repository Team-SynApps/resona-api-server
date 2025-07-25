package synapps.resona.api.oauth.handler;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import synapps.resona.api.global.properties.AppProperties;
import synapps.resona.api.mysql.member.entity.account.RoleType;
import synapps.resona.api.mysql.member.entity.member.MemberRefreshToken;
import synapps.resona.api.mysql.member.repository.member.MemberRefreshTokenRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.token.AuthToken;
import synapps.resona.api.mysql.token.AuthTokenProvider;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.info.OAuth2UserInfo;
import synapps.resona.api.oauth.info.OAuth2UserInfoFactory;
import synapps.resona.api.oauth.respository.CustomOAuth2AuthorizationRequestRepository;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final AuthTokenProvider tokenProvider;
  private final AppProperties appProperties;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;
  private final CustomOAuth2AuthorizationRequestRepository authorizationRequestRepository;
  private final MemberService memberService;

  @Value("${oauth.redirect-scheme}")
  private String redirectScheme;


  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    String targetUrl = determineTargetUrl(request, response, authentication);

    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    clearAuthenticationAttributes(request, response);
    redirectToScheme(response, targetUrl);
  }

  protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    String baseRedirectUri = (String) request.getSession().getAttribute(redirectScheme);

    OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
    ProviderType providerType = ProviderType.valueOf(
        authToken.getAuthorizedClientRegistrationId().toUpperCase());

    OidcUser user = ((OidcUser) authentication.getPrincipal());
    OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType,
        user.getAttributes());
    Collection<? extends GrantedAuthority> authorities = ((OidcUser) authentication.getPrincipal()).getAuthorities();

    RoleType roleType =
        hasAuthority(authorities, RoleType.ADMIN.getCode()) ? RoleType.ADMIN : RoleType.USER;

    Date now = new Date();
    AuthToken accessToken = tokenProvider.createAuthToken(
        userInfo.getEmail(),
        roleType.getCode(),
        new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
    );

    long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
    AuthToken refreshToken = tokenProvider.createAuthToken(
        appProperties.getAuth().getTokenSecret(),
        new Date(now.getTime() + refreshTokenExpiry)
    );

    MemberRefreshToken userRefreshToken = memberRefreshTokenRepository.findByMemberEmail(
        userInfo.getEmail());
    if (userRefreshToken != null) {
      userRefreshToken.setRefreshToken(refreshToken.getToken());
      memberRefreshTokenRepository.saveAndFlush(userRefreshToken);
    } else {
      userRefreshToken = new MemberRefreshToken(userInfo.getEmail(), refreshToken.getToken());
      memberRefreshTokenRepository.saveAndFlush(userRefreshToken);
    }

    Map<String, Object> queryParams = new HashMap<>();
    queryParams.put("registered", memberService.isRegisteredMember(userInfo.getEmail()));
    queryParams.put("accessToken", accessToken.getToken());
    queryParams.put("refreshToken", refreshToken.getToken());

    return createRedirectScheme(baseRedirectUri, queryParams);
  }

  private String createRedirectScheme(String baseRedirectUri, Map<String, Object> queryParams) {
    StringBuilder stringBuffer = new StringBuilder()
        .append(baseRedirectUri)
        .append("://");
    for (String key : queryParams.keySet()) {
      stringBuffer.append(key);
      stringBuffer.append("=");
      stringBuffer.append(queryParams.get(key));
      stringBuffer.append("&");
    }
    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    return stringBuffer.toString();
  }

  private void redirectToScheme(HttpServletResponse response, String redirectUri)
      throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpStatus.FOUND.value());
    response.sendRedirect(redirectUri);
  }


  protected void clearAuthenticationAttributes(HttpServletRequest request,
      HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

  private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities,
      String authority) {
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
          return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
              && authorizedURI.getPort() == clientRedirectUri.getPort();
        });
  }

//    protected ResponseEntity<?> getResponse(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        Optional<String> redirectUri = CookieUtil.getCookie(request, OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
//                .map(Cookie::getValue);
//
//        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
//            throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
//        }
//
//        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
//
//        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
//        ProviderType providerType = ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());
//
//        OidcUser user = ((OidcUser) authentication.getPrincipal());
//        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
//        Collection<? extends GrantedAuthority> authorities = ((OidcUser) authentication.getPrincipal()).getAuthorities();
//
//        RoleType roleType = hasAuthority(authorities, RoleType.ADMIN.getCode()) ? RoleType.ADMIN : RoleType.USER;
//
//        Date now = new Date();
//        AuthToken accessToken = tokenProvider.createAuthToken(
//                userInfo.getEmail(),
//                roleType.getCode(),
//                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
//        );
//
//        // refresh 토큰 설정
//        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
//
//        AuthToken refreshToken = tokenProvider.createAuthToken(
//                appProperties.getAuth().getTokenSecret(),
//                new Date(now.getTime() + refreshTokenExpiry)
//        );
//
//        // DB 저장
//        MemberRefreshToken userRefreshToken = memberRefreshTokenRepository.findByMemberEmail(userInfo.getId());
//        if (userRefreshToken != null) {
//            userRefreshToken.setRefreshToken(refreshToken.getToken());
//        } else {
//            userRefreshToken = new MemberRefreshToken(userInfo.getEmail(), refreshToken.getToken());
//            memberRefreshTokenRepository.saveAndFlush(userRefreshToken);
//        }
//
//        int cookieMaxAge = (int) refreshTokenExpiry / 60;
//
//        CookieUtil.deleteCookie(request, response, OAuth2AuthorizationRequestBasedOnCookieRepository.REFRESH_TOKEN);
//        CookieUtil.addCookie(response, OAuth2AuthorizationRequestBasedOnCookieRepository.REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);
//
//        MetaDataDto metaData = MetaDataDto.createSuccessMetaData(request.getQueryString(), "1","api server");
//        ResponseDto responseData = new ResponseDto(metaData, List.of(accessToken.getToken()));
//
//        return ResponseEntity.ok(responseData);
//    }
}


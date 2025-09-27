package com.synapps.resona.oauth.handler;


import com.synapps.resona.entity.account.RoleType;
import com.synapps.resona.entity.member.MemberRefreshToken;
import com.synapps.resona.properties.AppProperties;
import com.synapps.resona.entity.token.AuthToken;
import com.synapps.resona.entity.token.AuthTokenProvider;
import com.synapps.resona.utils.CookieUtil;
import com.synapps.resona.entity.account.ProviderType;
import com.synapps.resona.oauth.info.OAuth2UserInfo;
import com.synapps.resona.oauth.info.OAuth2UserInfoFactory;
import com.synapps.resona.oauth.respository.CustomOAuth2AuthorizationRequestRepository;
import com.synapps.resona.repository.member.MemberRefreshTokenRepository;
import com.synapps.resona.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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

    Optional<String> redirectUriCookie = CookieUtil.getCookie(request, CustomOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
        .map(Cookie::getValue);

    if(redirectUriCookie.isPresent() && !isAuthorizedRedirectUri(redirectUriCookie.get())) {
      try {
        // 허용되지 않은 URI면 에러 처리
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unauthorized Redirect URI");
      } catch (IOException e) {
        logger.error("Error sending redirect URI error response", e);
      }
      throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication.");
    }

    String baseRedirectUri = redirectUriCookie.orElse(redirectScheme);

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
    queryParams.put("email", userInfo.getEmail());

    return createRedirectScheme(baseRedirectUri, queryParams);
  }

  private String createRedirectScheme(String baseRedirectUri, Map<String, Object> queryParams) {
    if (baseRedirectUri == null || baseRedirectUri.trim().isEmpty()) {
      logger.error(String.format("Base redirect URI is missing. Using default scheme '%s'.", redirectScheme));
      baseRedirectUri = redirectScheme;
    }

    StringBuilder stringBuffer = new StringBuilder()
        .append(baseRedirectUri)
        .append("://?");

    if (!queryParams.isEmpty()) {
      queryParams.forEach((key, value) -> {
        stringBuffer.append(key);
        stringBuffer.append("=");
        stringBuffer.append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8));
        stringBuffer.append("&");
      });
      stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    }

    return stringBuffer.toString();
  }

  private void redirectToScheme(HttpServletResponse response, String redirectUri)
      throws IOException {
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

  /**
   * 클라이언트가 전달한 redirect_uri가 허용된 URI인지 검증합니다.
   * 앱스킴의 경우 host, port가 없으므로 스킴 자체를 비교해야 합니다.
   */
  private boolean isAuthorizedRedirectUri(String uri) {
    return appProperties.getOauth2().getAuthorizedRedirectUris()
        .stream()
        .anyMatch(authorizedRedirectUri -> authorizedRedirectUri.equalsIgnoreCase(uri));
  }
}
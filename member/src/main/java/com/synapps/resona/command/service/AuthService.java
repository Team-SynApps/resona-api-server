package com.synapps.resona.command.service;


import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.command.dto.request.auth.AppleLoginRequest;
import com.synapps.resona.command.dto.request.auth.LoginRequest;
import com.synapps.resona.command.dto.request.auth.RefreshRequest;
import com.synapps.resona.command.dto.response.ChatMemberDto;
import com.synapps.resona.command.dto.response.OAuthPlatformMemberResponse;
import com.synapps.resona.command.dto.response.TokenResponse;
import com.synapps.resona.command.entity.account.AccountInfo;
import com.synapps.resona.command.entity.account.AccountStatus;
import com.synapps.resona.command.entity.account.RoleType;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.entity.member.MemberProvider;
import com.synapps.resona.command.entity.member.MemberRefreshToken;
import com.synapps.resona.command.entity.member_details.MemberDetails;
import com.synapps.resona.command.entity.account.ProviderType;
import com.synapps.resona.command.entity.member.UserPrincipal;
import com.synapps.resona.command.entity.profile.Profile;
import com.synapps.resona.command.entity.token.AuthToken;
import com.synapps.resona.command.entity.token.AuthTokenProvider;
import com.synapps.resona.event.MemberRegisteredEvent;
import com.synapps.resona.exception.AuthException;
import com.synapps.resona.properties.AppProperties;
import com.synapps.resona.utils.HeaderUtil;
import com.synapps.resona.oauth.apple.AppleOAuthUserProvider;
import com.synapps.resona.command.repository.member.MemberProviderRepository;
import com.synapps.resona.command.repository.member.MemberRefreshTokenRepository;
import com.synapps.resona.command.repository.member.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final static long THREE_DAYS_MSEC = 259200;
  private final AuthenticationManager authenticationManager;
  private final AppProperties appProperties;
  private final AuthTokenProvider tokenProvider;
  private final MemberRefreshTokenRepository memberRefreshTokenRepository;
  private final AppleOAuthUserProvider appleOAuthUserProvider;
  private final MemberService memberService;
  private final MemberRepository memberRepository;
  private final MemberProviderRepository memberProviderRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public TokenResponse login(LoginRequest loginRequest) {
    String memberEmail = loginRequest.getMemberEmail();
    String memberPassword = loginRequest.getPassword();

    Member member = memberRepository.findWithRegisterRelationsByEmail(memberEmail)
        .orElseThrow(() -> new AuthException(
            AuthErrorCode.LOGIN_FAILED.getMessage(),
            AuthErrorCode.LOGIN_FAILED.getStatus(),
            AuthErrorCode.LOGIN_FAILED.getCustomCode()
        ));

    memberProviderRepository.findByMemberAndProviderType(member, ProviderType.LOCAL)
        .orElseThrow(() -> new AuthException(
            AuthErrorCode.LOGIN_FAILED.getMessage(),
            AuthErrorCode.LOGIN_FAILED.getStatus(),
            AuthErrorCode.LOGIN_FAILED.getCustomCode()
        ));

    Authentication authentication = getAuthentication(memberEmail, memberPassword);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    Date now = new Date();
    long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

    AuthToken accessToken = createToken(memberEmail, authentication, now);
    AuthToken refreshToken = createRefreshToken(now, refreshTokenExpiry);

    checkRefreshToken(memberEmail, refreshToken);

    return new TokenResponse(accessToken, refreshToken,
        memberService.isRegisteredMember(memberEmail));
  }

  @Transactional
  public TokenResponse appleLogin(AppleLoginRequest loginRequest) throws Exception {
    OAuthPlatformMemberResponse applePlatformMember =
        appleOAuthUserProvider.getApplePlatformMember(loginRequest.getToken());

    String memberEmail = applePlatformMember.getEmail();
    String providerId = applePlatformMember.getPlatformId();

    Member member = memberRepository.findWithAccountInfoByEmail(memberEmail).orElse(null);

    boolean isNewUser = (member == null);

    if (isNewUser) {
      member = createMemberWithProvider(memberEmail, providerId, ProviderType.APPLE);
    } else {
      Member finalMember = member;
      memberProviderRepository.findByMemberAndProviderType(member, ProviderType.APPLE)
          .orElseGet(() -> {
            MemberProvider newProvider = MemberProvider.of(finalMember, ProviderType.APPLE, providerId);
            return memberProviderRepository.save(newProvider);
          });
    }

    // 소셜 로그인은 인증이 이미 완료된 상태이므로, UserPrincipal을 직접 생성하여 SecurityContext에 설정합니다.
    UserPrincipal principal = UserPrincipal.create(member, ProviderType.APPLE);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        principal,
        null,
        principal.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    Date now = new Date();
    long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
    AuthToken accessToken = createToken(memberEmail, authentication, now);
    AuthToken refreshToken = createRefreshToken(now, refreshTokenExpiry);

    checkRefreshToken(memberEmail, refreshToken);

    return new TokenResponse(accessToken, refreshToken, !isNewUser && memberService.isRegisteredMember(memberEmail));
  }

  private Member createMemberWithProvider(String email, String providerId, ProviderType providerType) {

    AccountInfo accountInfo = AccountInfo.of(RoleType.USER, AccountStatus.TEMPORARY);
    MemberDetails memberDetails = MemberDetails.empty();
    Profile profile = Profile.empty();
    Member newMember = Member.of(accountInfo,memberDetails, profile, email, null, LocalDateTime.now());

    memberRepository.save(newMember);

    eventPublisher.publishEvent(new MemberRegisteredEvent(newMember.getId()));

    MemberProvider memberProvider = MemberProvider.of(newMember, providerType, providerId);
    memberProviderRepository.save(memberProvider);

    return newMember;
  }

  @Transactional
  public TokenResponse refresh(HttpServletRequest request, RefreshRequest refreshRequest) {
    Date now = new Date();
    String accessToken = HeaderUtil.getAccessToken(request);
    AuthToken authToken = tokenProvider.convertAuthToken(accessToken);

    Claims claims = authToken.getExpiredTokenClaims();
    if (claims == null) {
      throw AuthException.accessTokenNotExpired();
    }

    String memberEmail = claims.getSubject();
    RoleType roleType = RoleType.of(claims.get("role", String.class));
    String refreshToken = refreshRequest.getRefreshToken();

    if (refreshToken == null) {
      throw AuthException.refreshTokenNotFound();
    }

    AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);
    if (!authRefreshToken.validate()) {
      throw AuthException.invalidRefreshToken();
    }

    MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository
        .findByMemberEmailAndRefreshToken(memberEmail, refreshToken);
    if (memberRefreshToken == null) {
      throw AuthException.invalidRefreshToken();
    }

//    long validTime = calculateValidTime(authRefreshToken);

    long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
    AuthToken newRefreshToken = createRefreshToken(now, refreshTokenExpiry);
    memberRefreshToken.setRefreshToken(newRefreshToken.getToken());

//    if (validTime <= THREE_DAYS_MSEC) {
//      long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
//      newRefreshToken = createRefreshToken(now, refreshTokenExpiry);
//      memberRefreshToken.setRefreshToken(newRefreshToken.getToken());
//    }

    AuthToken newAccessToken = createNewAccessToken(memberEmail, roleType, now);
    return new TokenResponse(newAccessToken, newRefreshToken, true);
  }


  public ChatMemberDto isMember(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = HeaderUtil.getAccessToken(request);
    AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
    Claims claims = authToken.getTokenClaims();
    String memberEmail = claims.getSubject();
    if (memberRepository.existsByEmail(memberEmail)) {
      return new ChatMemberDto(memberEmail, true);
    }
    return new ChatMemberDto("", false);
  }


  private Authentication getAuthentication(String memberEmail, String memberPassword) {
    return authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            memberEmail,
            memberPassword)
    );
  }

  private AuthToken createToken(String memberEmail, Authentication authentication,
      Date currentTime) {
    return tokenProvider.createAuthToken(
        memberEmail,
        ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
        new Date(currentTime.getTime() + appProperties.getAuth().getTokenExpiry())
    );
  }

  private AuthToken createRefreshToken(Date currentTime, long refreshTokenExpiry) {
    return tokenProvider.createAuthToken(
        appProperties.getAuth().getTokenSecret(),
        new Date(currentTime.getTime() + refreshTokenExpiry)
    );
  }

  private AuthToken createNewAccessToken(String memberEmail, RoleType roleType, Date currentTime) {
    return tokenProvider.createAuthToken(
        memberEmail,
        roleType.getCode(),
        new Date(currentTime.getTime() + appProperties.getAuth().getTokenExpiry())
    );
  }

  private void checkRefreshToken(String memberEmail, AuthToken refreshToken) {
    MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberEmail(
        memberEmail);
    if (memberRefreshToken == null) { // refresh token이 없는 경우 새로 등록
      memberRefreshToken = new MemberRefreshToken(memberEmail, refreshToken.getToken());
      memberRefreshTokenRepository.saveAndFlush(memberRefreshToken);
    } else { // DB에 refresh token 업데이트
      memberRefreshToken.setRefreshToken(refreshToken.getToken());
    }
  }

  private long calculateValidTime(AuthToken authToken) {
    Date now = new Date();
    return authToken.getTokenClaims().getExpiration().getTime() - now.getTime();
  }
}
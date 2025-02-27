package synapps.resona.api.mysql.member.service;


import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.global.properties.AppProperties;
import synapps.resona.api.global.utils.HeaderUtil;
import synapps.resona.api.mysql.member.dto.request.auth.AppleLoginRequest;
import synapps.resona.api.mysql.member.dto.request.auth.LoginRequest;
import synapps.resona.api.mysql.member.dto.request.auth.RefreshRequest;
import synapps.resona.api.mysql.member.dto.response.ChatMemberDto;
import synapps.resona.api.mysql.member.dto.response.OAuthPlatformMemberResponse;
import synapps.resona.api.mysql.member.dto.response.TokenResponse;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member.MemberRefreshToken;
import synapps.resona.api.mysql.member.entity.account.RoleType;
import synapps.resona.api.mysql.member.exception.AuthException;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.repository.AccountInfoRepository;
import synapps.resona.api.mysql.member.repository.MemberRefreshTokenRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.token.AuthToken;
import synapps.resona.api.mysql.token.AuthTokenProvider;
import synapps.resona.api.oauth.apple.AppleOAuthUserProvider;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.UserPrincipal;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final static long THREE_DAYS_MSEC = 259200;
    private final AuthenticationManager authenticationManager;
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final MemberRefreshTokenRepository memberRefreshTokenRepository;
    private final MemberRepository memberRepository;
    private final AccountInfoRepository accountInfoRepository;
    private final AppleOAuthUserProvider appleOAuthUserProvider;
//    private final static String REFRESH_TOKEN = "refresh_token";

    @Transactional
    public ResponseEntity<?> login(HttpServletRequest request,
                                   HttpServletResponse response,
                                   LoginRequest loginRequest) {
        String memberEmail = loginRequest.getMemberEmail();
        String memberPassword = loginRequest.getPassword();

        Authentication authentication = getAuthentication(memberEmail, memberPassword);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Date now = new Date();
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        AuthToken accessToken = createToken(memberEmail, authentication, now);
        AuthToken refreshToken = createRefreshToken(now, refreshTokenExpiry);

        checkRefreshToken(memberEmail, refreshToken);

        MetaDataDto metaData = MetaDataDto.createSuccessMetaData(request.getQueryString(), "1", "api server");
        ResponseDto responseData = new ResponseDto(metaData, List.of(new TokenResponse(accessToken, refreshToken)));
        return ResponseEntity.ok(responseData);
    }

    @Transactional
    public ResponseEntity<?> appleLogin(HttpServletRequest request, HttpServletResponse response, AppleLoginRequest loginRequest) throws Exception {
        OAuthPlatformMemberResponse applePlatformMember =
                appleOAuthUserProvider.getApplePlatformMember(loginRequest.getToken());

        String memberEmail = applePlatformMember.getEmail();
        String memberPassword = applePlatformMember.getPlatformId();

        if (memberRepository.existsByEmail(applePlatformMember.getEmail())) {
            Authentication authentication = getAuthentication(memberEmail, memberPassword);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Date now = new Date();
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

            AuthToken accessToken = createToken(memberEmail, authentication, now);
            AuthToken refreshToken = createRefreshToken(now, refreshTokenExpiry);

            checkRefreshToken(memberEmail, refreshToken);

            MetaDataDto metaData = MetaDataDto.createSuccessMetaData(request.getQueryString(), "1", "api server");
            ResponseDto responseData = new ResponseDto(metaData, List.of(new TokenResponse(accessToken, refreshToken)));
            return ResponseEntity.ok(responseData);

        } else {
            Member member = Member.of(
                    applePlatformMember.getEmail(),
                    applePlatformMember.getPlatformId(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            AccountInfo accountInfo = AccountInfo.of(
                    member,
                    RoleType.USER,
                    ProviderType.APPLE,
                    AccountStatus.ACTIVE,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            member.encodePassword(applePlatformMember.getPlatformId());
            memberRepository.save(member);
            accountInfoRepository.save(accountInfo);

            Authentication authentication = getAuthentication(memberEmail, memberPassword);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

            Date now = new Date();

            AuthToken accessToken = createToken(memberEmail, authentication, now);
            AuthToken refreshToken = createRefreshToken(now, refreshTokenExpiry);

            checkRefreshToken(memberEmail, refreshToken);

            MetaDataDto metaData = MetaDataDto.createSuccessMetaData(request.getQueryString(), "1", "api server");
            ResponseDto responseData = new ResponseDto(metaData, List.of(new TokenResponse(accessToken, refreshToken)));
            return ResponseEntity.ok(responseData);
        }
    }

    @Transactional
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response, RefreshRequest refreshRequest) {
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

        MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberEmailAndRefreshToken(memberEmail, refreshToken);
        if (memberRefreshToken == null) {
            throw AuthException.invalidRefreshToken();
        }

        long validTime = calculateValidTime(authRefreshToken);
        AuthToken newRefreshToken = authRefreshToken;

        if (validTime <= THREE_DAYS_MSEC) {
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
            newRefreshToken = createRefreshToken(now, refreshTokenExpiry);
            memberRefreshToken.setRefreshToken(newRefreshToken.getToken());
        }

        AuthToken newAccessToken = createNewAccessToken(memberEmail, roleType, now);

        MetaDataDto metaData = MetaDataDto.createSuccessMetaData(request.getQueryString(), "1", "api server");
        ResponseDto responseDto = new ResponseDto(metaData, List.of(new TokenResponse(newAccessToken, newRefreshToken)));
        return ResponseEntity.ok(responseDto);
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


    private AuthToken createToken(String memberEmail, Authentication authentication, Date currentTime) {
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(MemberException::memberNotFound);
        AccountInfo accountInfo = accountInfoRepository.findByMember(member);

        return tokenProvider.createAuthToken(
                memberEmail,
//                ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
//                ((Member) authentication.getPrincipal()).getAccountInfo().getRoleType().getCode(),
                accountInfo.getRoleType().getCode(),
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
        MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberEmail(memberEmail);
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

//    private void executeCookie(HttpServletRequest request,
//                               HttpServletResponse response,
//                               long refreshTokenExpiry,
//                               AuthToken refreshToken) {
//        int cookieMaxAge = (int) refreshTokenExpiry / 60;
//        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
//        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);
//    }

}
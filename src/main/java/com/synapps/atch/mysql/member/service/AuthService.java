package com.synapps.atch.mysql.member.service;


import com.synapps.atch.global.dto.MetaDataDto;
import com.synapps.atch.global.dto.ResponseDto;
import com.synapps.atch.global.dto.ResponseHeader;
import com.synapps.atch.global.properties.AppProperties;
import com.synapps.atch.global.utils.CookieUtil;
import com.synapps.atch.global.utils.HeaderUtil;
import com.synapps.atch.mysql.member.dto.request.LoginRequest;
import com.synapps.atch.mysql.member.dto.response.ChatMemberDto;
import com.synapps.atch.mysql.member.entity.MemberRefreshToken;
import com.synapps.atch.mysql.member.repository.MemberRefreshTokenRepository;
import com.synapps.atch.mysql.member.repository.MemberRepository;
import com.synapps.atch.oauth.entity.RoleType;
import com.synapps.atch.oauth.entity.UserPrincipal;
import com.synapps.atch.oauth.token.AuthToken;
import com.synapps.atch.oauth.token.AuthTokenProvider;
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

import jakarta.servlet.http.Cookie;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final MemberRefreshTokenRepository memberRefreshTokenRepository;
//    private final AppleOAuthUserProvider appleOAuthUserProvider;
    private final MemberRepository memberRepository;

    private final static long THREE_DAYS_MSEC = 259200;
    private final static String REFRESH_TOKEN = "refresh_token";

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
        AuthToken refreshToken = getRefreshToken(now, refreshTokenExpiry);

        checkRefreshToken(memberEmail, refreshToken);
        executeCookie(request, response, refreshTokenExpiry, refreshToken);

        // hard coded datas here
        MetaDataDto metaData = MetaDataDto.createSuccessMetaData(request.getQueryString(), "1","api server");
        ResponseDto responseData = new ResponseDto(metaData, List.of(accessToken));
        return ResponseEntity.ok(responseData);
    }
//
//    @Transactional
//    public ResponseEntity<?> appleOAuthLogin(HttpServletRequest request,
//                                             HttpServletResponse response,
//                                             AppleLoginRequest appleLoginRequest) {
//        OAuthPlatformMemberResponse applePlatformMember =
//                appleOAuthUserProvider.getApplePlatformMember(appleLoginRequest.getToken());
//
//        String email = applePlatformMember.getEmail();
//        String platformId = applePlatformMember.getPlatformId();
//        if (!userRepository.existsByUserId(platformId)) {
//            User user = User.of(
//                    platformId,
//                    "default",
//                    email,
//                    ProviderType.APPLE,
//                    RoleType.USER,
//                    LocalDateTime.now(),
//                    LocalDateTime.now()
//            );
//            user.encodePassword(platformId);
//            levelRepository.saveAndFlush(Level.newLevel(user));
//        }
//
//        // Create authentication based on retrieved Apple platform member details
//        Authentication authentication = getAuthentication(platformId, platformId);
//
//        // Set authentication in Security Context
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        Date now = new Date();
//        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
//
//        // Generate access and refresh tokens
//        AuthToken accessToken = createToken(email, authentication, now);
//        AuthToken refreshToken = getRefreshToken(now, refreshTokenExpiry);
//
//        // Check and handle refresh token
//        checkRefreshToken(email, refreshToken);
//        executeCookie(request, response, refreshTokenExpiry, refreshToken);
//
//        // Prepare response data
//        ResponseDto responseData = new ResponseDto(true, List.of(accessToken));
//        return ResponseEntity.ok(responseData);
//    }
//

    @Transactional
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        Date now = new Date();
        // access token 확인
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);

        // expired access token 인지 확인
        Claims claims = authToken.getExpiredTokenClaims();

        if (claims == null) {
            return errorResponse("아직 accessToken이 만료되지 않았습니다.");
        }

        String memberEmail = claims.getSubject();
        RoleType roleType = RoleType.of(claims.get("role", String.class));

        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));

        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);

        if (!authRefreshToken.validate()) {
            return errorResponse("refreshToken이 올바르지 않습니다.");
        }

        // userId refresh token 으로 DB 확인
        MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberEmailAndRefreshToken(memberEmail, refreshToken);

        if (memberRefreshToken == null) {
            return errorResponse("refreshToken이 올바르지 않습니다.");
        }

        long validTime = calculateValidTime(authRefreshToken);

        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
        if (validTime <= THREE_DAYS_MSEC) {
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
            authRefreshToken = getRefreshToken(now, refreshTokenExpiry);
            memberRefreshToken.setRefreshToken(authRefreshToken.getToken());

            executeCookie(request, response, refreshTokenExpiry, authRefreshToken);
        }
        MetaDataDto metaData = MetaDataDto.createSuccessMetaData(request.getQueryString(), "1","api server");
        ResponseDto responseDto = new ResponseDto(metaData,List.of(createNewAccessToken(memberEmail, roleType, now).getToken()));
        return ResponseEntity.ok(responseDto);
    }

    public ChatMemberDto isMember(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
        Claims claims = authToken.getTokenClaims();
        String memberEmail = claims.getSubject();
        if(memberRepository.existsByEmail(memberEmail)){
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
        return tokenProvider.createAuthToken(
                memberEmail,
                ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
                new Date(currentTime.getTime() + appProperties.getAuth().getTokenExpiry())
        );
    }

    private AuthToken getRefreshToken(Date currentTime, long refreshTokenExpiry) {
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
        if (memberRefreshToken == null) {
            // 없는 경우 새로 등록
            memberRefreshToken = new MemberRefreshToken(memberEmail, refreshToken.getToken());
            memberRefreshTokenRepository.saveAndFlush(memberRefreshToken);
        } else {
            // DB에 refresh 토큰 업데이트
            memberRefreshToken.setRefreshToken(refreshToken.getToken());
        }
    }

    private void executeCookie(HttpServletRequest request,
                               HttpServletResponse response,
                               long refreshTokenExpiry,
                               AuthToken refreshToken) {
        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);
    }
    private long calculateValidTime(AuthToken authToken) {
        Date now = new Date();
        return authToken.getTokenClaims().getExpiration().getTime() - now.getTime();
    }
    private ResponseEntity<?> errorResponse(String message) {
        return ResponseEntity.ok(new ResponseHeader(500, message));
    }
}
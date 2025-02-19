package synapps.resona.api.mysql.member.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.global.properties.AppProperties;
import synapps.resona.api.mysql.member.dto.response.TokenResponse;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.AccountInfoRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.mysql.member.entity.member.RoleType;
import synapps.resona.api.mysql.token.AuthToken;
import synapps.resona.api.mysql.token.AuthTokenProvider;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class TempTokenService {
    private final AuthTokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final AccountInfoRepository accountInfoRepository;
    private final AppProperties appProperties;

    @Transactional
    public TokenResponse createTemporaryToken(HttpServletRequest request, HttpServletResponse response, String email) {
        if (!memberRepository.existsByEmail(email)) {
            // 새로운 멤버 생성
            Member newMember = Member.of(
                    email,
                    generateRandomPassword(), // 임시 비밀번호 생성
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            // AccountInfo 생성
            AccountInfo accountInfo = AccountInfo.of(
                    newMember,
                    RoleType.GUEST,
                    ProviderType.LOCAL,
                    AccountStatus.TEMPORARY,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            // 비밀번호 인코딩 및 저장
            newMember.encodePassword(newMember.getPassword());
            memberRepository.save(newMember);
            accountInfoRepository.save(accountInfo);
        }

        Date now = new Date();

        // 6시간 유효한 access token 생성
        AuthToken accessToken = tokenProvider.createAuthToken(
                email,
                RoleType.GUEST.getCode(),
                new Date(now.getTime() + TimeUnit.HOURS.toMillis(6))
        );

        // refresh token은 24시간으로 설정
        long refreshTokenExpiry = TimeUnit.HOURS.toMillis(24);
        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        return new TokenResponse(accessToken, refreshToken);
    }

    // 임시 토큰 유효성 검증
    public boolean isTemporaryToken(String token) {
        AuthToken authToken = tokenProvider.convertAuthToken(token);
        if (!authToken.validate()) {
            return false;
        }

        Claims claims = authToken.getTokenClaims();
        String email = claims.getSubject();
        return email.startsWith("temp_") && email.endsWith("@temp.com");
    }

    // 만료된 임시 계정 정리를 위한 스케줄러 메소드
    @Scheduled(cron = "0 0 * * * *") // 매시간 실행
    @Transactional
    public void cleanupExpiredTemporaryAccounts() {
        LocalDateTime now = LocalDateTime.now();
        List<AccountInfo> expiredAccounts = accountInfoRepository
                .findExpiredTemporaryAccounts(AccountStatus.TEMPORARY, now);

        for (AccountInfo account : expiredAccounts) {
            Member member = account.getMember();
            accountInfoRepository.delete(account);
            memberRepository.delete(member);
        }
    }

    private String generateRandomPassword() {
        // 임시 비밀번호 생성 로직
        return UUID.randomUUID().toString();
    }
}
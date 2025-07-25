package synapps.resona.api.mysql.member.service;

import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.global.properties.AppProperties;
import synapps.resona.api.mysql.member.dto.response.TempTokenResponse;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.account.RoleType;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.exception.AccountInfoException;
import synapps.resona.api.mysql.member.repository.account.AccountInfoRepository;
import synapps.resona.api.mysql.member.repository.member.MemberRepository;
import synapps.resona.api.mysql.token.AuthToken;
import synapps.resona.api.mysql.token.AuthTokenProvider;
import synapps.resona.api.oauth.entity.ProviderType;


@Service
@RequiredArgsConstructor
public class TempTokenService {

  private final AuthTokenProvider tokenProvider;
  private final MemberRepository memberRepository;
  private final AccountInfoRepository accountInfoRepository;
  private final AuthenticationManager authenticationManager;
  private final AppProperties appProperties;

  @Transactional
  public TempTokenResponse createTemporaryToken(String email) {
    boolean isRegistered = true;
    if (!memberRepository.existsByEmail(email)) {
      AccountInfo accountInfo = AccountInfo.of(
          RoleType.GUEST,
          ProviderType.LOCAL,
          AccountStatus.TEMPORARY
      );

      MemberDetails memberDetails = MemberDetails.empty();
      Profile profile = Profile.empty();
      // 새로운 멤버 생성
      Member newMember = Member.of(
          accountInfo,
          memberDetails,
          profile,
          email,
          generateRandomPassword(), // 임시 비밀번호 생성
          LocalDateTime.now()
      );

      // AccountInfo 생성

      // 비밀번호 인코딩 및 저장
      newMember.encodePassword(newMember.getPassword());
      memberRepository.save(newMember);
      accountInfoRepository.save(accountInfo);
    }

    AccountInfo accountInfo = memberRepository.findAccountInfoByEmail(email)
        .orElseThrow(AccountInfoException::accountInfoNotFound);

    if (accountInfo.isAccountTemporary()) {
      isRegistered = false;
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

    return new TempTokenResponse(accessToken, refreshToken, isRegistered);
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
//    @Scheduled(cron = "0 0 * * * *") // 매시간 실행
//    @Transactional
//    public void cleanupExpiredTemporaryAccounts() {
//        LocalDateTime now = LocalDateTime.now();
//        List<AccountInfo> expiredAccounts = accountInfoRepository
//                .findExpiredTemporaryAccounts(AccountStatus.TEMPORARY, now);
//
//        for (AccountInfo account : expiredAccounts) {
//            Member member = account.getMember();
//            accountInfoRepository.delete(account);
//            memberRepository.delete(member);
//        }
//    }

  private String generateRandomPassword() {
    // 임시 비밀번호 생성 로직
    return UUID.randomUUID().toString();
  }

  private Authentication getAuthentication(String memberEmail, String memberPassword) {
    return authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            memberEmail,
            memberPassword)
    );
  }
}
package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import synapps.resona.api.IntegrationTestSupport;
import synapps.resona.api.global.properties.AppProperties;
import synapps.resona.api.mysql.member.dto.response.TempTokenResponse;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.account.RoleType;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.repository.AccountInfoRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.token.AuthTokenProvider;
import synapps.resona.api.oauth.entity.ProviderType;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;

class TempTokenServiceTest extends IntegrationTestSupport {
    @Autowired
    private TempTokenService tempTokenService;

    @Autowired
    private AuthTokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AppProperties appProperties;

    private String testEmail;
    private Member testMember;
    private AccountInfo testAccountInfo;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testAccountInfo = AccountInfo.of(RoleType.GUEST, ProviderType.LOCAL, AccountStatus.TEMPORARY);
        MemberDetails emptyMemberDetails = MemberDetails.empty();
        Profile emptyProfile = Profile.empty();
        testMember = Member.of(testAccountInfo, emptyMemberDetails, emptyProfile, testEmail, "password123", LocalDateTime.now());

        memberRepository.save(testMember);
        accountInfoRepository.save(testAccountInfo);
    }

    @Test
    @Transactional
    @DisplayName("임시 토큰을 정상적으로 발급한다.")
    void createTemporaryToken() {
        // When
        TempTokenResponse response = tempTokenService.createTemporaryToken(testEmail);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
        assertThat(response.isRegistered()).isFalse(); // 새로운 유저이므로 false여야 함

        // DB 확인
        Optional<Member> savedMember = memberRepository.findWithAllRelationsByEmail(testEmail);
        assertThat(savedMember).isPresent();
        Optional<AccountInfo> savedAccount = Optional.ofNullable(savedMember.get().getAccountInfo());
        assertThat(savedAccount).isPresent();
        assertThat(savedAccount.get().getRoleType()).isEqualTo(RoleType.GUEST);
        assertThat(savedAccount.get().getStatus()).isEqualTo(AccountStatus.TEMPORARY);
    }
}
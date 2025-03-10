package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import synapps.resona.api.IntegrationTestSupport;
import synapps.resona.api.mysql.member.dto.request.auth.SignupRequest;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member.RoleType;
import synapps.resona.api.mysql.member.repository.AccountInfoRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Transactional
class MemberServiceTest extends IntegrationTestSupport {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    private Member testMember;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 데이터 삽입
        testMember = Member.of(
                "test1@example.com",
                "password1234",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        testMember.encodePassword("password1234");

        AccountInfo accountInfo = AccountInfo.of(
                testMember,
                RoleType.USER,
                synapps.resona.api.oauth.entity.ProviderType.LOCAL,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        memberRepository.save(testMember);
        accountInfoRepository.save(accountInfo);

        // SecurityContext에 사용자 정보 설정
        setAuthentication(testMember.getEmail());
    }

    private void setAuthentication(String email) {
        User userPrincipal = new User(email, "", java.util.Collections.emptyList());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
    }

    @Test
    @DisplayName("회원 정보 조회 테스트")
    void testGetMember() {
        // when
        MemberDto memberDto = memberService.getMember();

        // then
        assertThat(memberDto).isNotNull();
        assertThat(memberDto.getEmail()).isEqualTo(testMember.getEmail());
    }

    @Test
    @DisplayName("회원 상세 정보 조회 테스트")
    void testGetMemberDetailInfo() {
        // when
        var memberDetailInfo = memberService.getMemberDetailInfo();

        // then
        assertThat(memberDetailInfo).isNotNull();
        assertThat(memberDetailInfo.getRoleType()).isEqualTo(RoleType.USER.toString());
    }

    @Test
    @DisplayName("회원 가입 테스트")
    void testSignUp() throws Exception {
        // given
        SignupRequest request = new SignupRequest("newuser1@example.com", "newpassword");

        // when
        MemberDto newMember = memberService.signUp(request);

        // then
        assertThat(newMember).isNotNull();
        assertThat(newMember.getEmail()).isEqualTo("newuser1@example.com");
        assertThat(memberRepository.existsByEmail("newuser1@example.com")).isTrue();
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void testDeleteUser() {
        // when
        String result = memberService.deleteUser();

        // then
        assertThat(result).isEqualTo("delete successful");
        assertThat(testMember.isDeleted()).isTrue();
    }
}
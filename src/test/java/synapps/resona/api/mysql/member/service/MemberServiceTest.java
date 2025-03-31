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
import synapps.resona.api.mysql.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.dto.response.MemberRegisterResponseDto;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member.RoleType;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.repository.AccountInfoRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.Set;

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
                LocalDateTime.now()
        );
        testMember.encodePassword("password1234");

        AccountInfo accountInfo = AccountInfo.of(
                testMember,
                RoleType.USER,
                synapps.resona.api.oauth.entity.ProviderType.LOCAL,
                AccountStatus.ACTIVE
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
    @DisplayName("회원 정보를 조회한다.")
    void testGetMember() {
        // when
        MemberDto memberDto = memberService.getMember();

        // then
        assertThat(memberDto).isNotNull();
        assertThat(memberDto.getEmail()).isEqualTo(testMember.getEmail());
    }

    @Test
    @DisplayName("회원 상세 정보를 조회한다.")
    void testGetMemberDetailInfo() {
        // when
        var memberDetailInfo = memberService.getMemberDetailInfo();

        // then
        assertThat(memberDetailInfo).isNotNull();
        assertThat(memberDetailInfo.getRoleType()).isEqualTo(RoleType.USER.toString());
    }

    @Test
    @DisplayName("회원 가입을 한다.")
    void testSignUp() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest(
                "newuser1@example.com",           // email
                "Newpass1@",                      // password (8~30 자리, 알파벳, 숫자, 특수문자 포함)
                CountryCode.KR,                   // nationality (예시: 한국)
                CountryCode.US,                   // countryOfResidence (예시: 미국)
                Set.of(Language.KOREAN),          // nativeLanguages (예시: 한국어)
                Set.of(Language.ENGLISH),         // interestingLanguages (예시: 영어)
                9,                                // timezone (예시: UTC+9)
                "1990-01-01",                     // birth (yyyy-MM-dd 형식)
                "newuser",                        // nickname (최대 15자)
                "http://example.com/profile.jpg"  // profileImageUrl
        );


        // when
        MemberRegisterResponseDto newMember = memberService.signUp(request);

        // then
        assertThat(newMember).isNotNull();
        assertThat(newMember.getEmail()).isEqualTo("newuser1@example.com");
        assertThat(memberRepository.existsByEmail("newuser1@example.com")).isTrue();
    }

    @Test
    @DisplayName("회원을 삭제한다.")
    void testDeleteUser() {
        // when
        String result = memberService.deleteUser();

        // then
        assertThat(result).isEqualTo("delete successful");
        assertThat(testMember.isDeleted()).isTrue();
    }
}
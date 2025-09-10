package synapps.resona.api.member.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import synapps.resona.api.support.IntegrationTestSupport;
import synapps.resona.api.chat.entity.ChatMember;
import synapps.resona.api.chat.repository.ChatMemberRepository;
import synapps.resona.api.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.member.dto.response.MemberRegisterResponseDto;
import synapps.resona.api.member.entity.account.AccountInfo;
import synapps.resona.api.member.entity.account.AccountStatus;
import synapps.resona.api.member.entity.account.RoleType;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.entity.member_details.MemberDetails;
import synapps.resona.api.member.entity.profile.CountryCode;
import synapps.resona.api.global.entity.Language;
import synapps.resona.api.member.entity.profile.Profile;
import synapps.resona.api.member.repository.member.MemberProviderRepository;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.oauth.entity.UserPrincipal;


@Sql("/cleanup.sql")
class MemberServiceTest extends IntegrationTestSupport {

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private ChatMemberRepository chatMemberRepository;

  @Autowired
  private MemberProviderRepository memberProviderRepository;

  private Member testMember;
  private Member guestMember;

  @BeforeEach
  void setUp() {
    AccountInfo accountInfo = AccountInfo.of(
        RoleType.USER,
        AccountStatus.ACTIVE
    );
    AccountInfo tempAccountInfo = AccountInfo.of(
        RoleType.GUEST,
        AccountStatus.TEMPORARY
    );

    testMember = Member.of(
        accountInfo,
        MemberDetails.empty(),
        Profile.empty(),
        "test1@example.com",
        "password1234",
        LocalDateTime.now()
    );

    guestMember = Member.of(
        tempAccountInfo,
        MemberDetails.empty(),
        Profile.empty(),
        "newuser1@example.com",
        "Newpass1@",
        LocalDateTime.now()
    );

    testMember.encodePassword("password1234");

    memberRepository.saveAll(List.of(testMember, guestMember));
    // SecurityContext에 사용자 정보 설정
    setAuthentication(testMember.getEmail());
  }

  private void setAuthentication(String email) {
    Member member = memberRepository.findByEmailWithAccountInfo(email)
        .orElseThrow(() -> new RuntimeException("테스트 유저 '" + email + "'를 찾을 수 없습니다."));

    UserPrincipal principal = UserPrincipal.create(member);

    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        principal, null, principal.getAuthorities());
    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(auth);
  }

  @Test
  @Transactional
  @DisplayName("회원 상세 정보를 조회한다.")
  void testGetMemberDetailInfo() {
    // when
    var memberDetailInfo = memberService.getMemberDetailInfo("test1@example.com");

    // then
    assertThat(memberDetailInfo).isNotNull();
    assertThat(memberDetailInfo.getRoleType()).isEqualTo(RoleType.USER.toString());
  }

  @Test
  @Transactional
  @DisplayName("회원 가입을 한다.")
  void testSignUp() throws Exception {
    // given
    RegisterRequest request = new RegisterRequest(
        "newuser1@example.com",           // email
        "newuser tag",                          // tag
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
  @Transactional
  @DisplayName("회원을 삭제한다.")
  void testDeleteUser() {
    // given
    String userEmail = testMember.getEmail();
    setAuthentication(userEmail); // 삭제할 유저로 인증 설정

    // when
    Map<String, String> result = memberService.deleteUser();

    // then
    assertThat(result).isEqualTo(Map.of("message", "User deleted successfully."));

    Optional<Member> foundMember = memberRepository.findByEmail(userEmail);
    assertThat(foundMember).isEmpty();
  }

  @Test
  @DisplayName("회원가입 시 MemberUpdatedEvent가 발행되고, MongoDB에 ChatMember가 동기화되어야 한다.")
  void signUp_ShouldSyncToChatMemberInMongoDB() {
    // given
    RegisterRequest request = new RegisterRequest(
        "newuser1@example.com",
        "newuser_tag",
        "Newpass1@",
        CountryCode.KR,
        CountryCode.US,
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        9,
        "1990-01-01",
        "MongoDB동기화테스트",
        "http://example.com/profile.jpg"
    );

    // when
    MemberRegisterResponseDto newMember = memberService.signUp(request);

    // then
    assertThat(newMember).isNotNull();
    assertThat(newMember.getEmail()).isEqualTo("newuser1@example.com");

    ChatMember chatMember = chatMemberRepository.findById(newMember.getMemberId()).orElse(null);

    assertThat(chatMember).isNotNull();
    assertThat(chatMember.getId()).isEqualTo(newMember.getMemberId());
    assertThat(chatMember.getNickname()).isEqualTo("MongoDB동기화테스트");
    assertThat(chatMember.getProfileImageUrl()).isEqualTo("http://example.com/profile.jpg");
  }
}
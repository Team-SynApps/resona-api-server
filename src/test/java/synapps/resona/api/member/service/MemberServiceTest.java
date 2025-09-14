package synapps.resona.api.member.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import synapps.resona.api.fixture.MemberFixture;
import synapps.resona.api.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.member.dto.response.MemberRegisterResponseDto;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.repository.member.MemberProviderRepository;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.oauth.entity.UserPrincipal;


@Sql("/cleanup.sql")
@Disabled("mongodb 환경 오류로 일단 비활성화합니다.")
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
    testMember = MemberFixture.createTestMember();
    guestMember = MemberFixture.createGuestMember();

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
    assertThat(memberDetailInfo.getRoleType()).isEqualTo("USER");
  }

  @Test
  @Transactional
  @DisplayName("회원 가입을 한다.")
  void testSignUp() throws Exception {
    // given
    RegisterRequest request = MemberFixture.createNewUserRegisterRequest();

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
    RegisterRequest request = MemberFixture.createMongoSyncRegisterRequest();

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

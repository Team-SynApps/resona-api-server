package synapps.resona.api.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import synapps.resona.api.fixture.MemberFixture;
import synapps.resona.api.support.IntegrationTestSupport;
import synapps.resona.api.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.member.dto.request.member_details.MemberDetailsRequest;
import synapps.resona.api.member.dto.response.MemberDetailsResponse;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.entity.member_details.MBTI;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Transactional
class MemberDetailsServiceTest extends IntegrationTestSupport {

  private final String email = "test@resona.com";

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberDetailsService memberDetailsService;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private TempTokenService tempTokenService;

  @BeforeEach
  void setUp() {
    // 1. 회원가입에 사용할 DTO 생성
    RegisterRequest request = MemberFixture.createDetailsTestRegisterRequest(email);

    // 2. 임시 회원 생성
    tempTokenService.createTemporaryToken(request.getEmail());

    // 3. 정식 회원 전환
    memberService.signUp(request);

    // 4. 테스트를 위한 인증 정보 설정
    Member member = memberRepository.findByEmailWithAccountInfo(email)
        .orElseThrow(() -> new RuntimeException("테스트 유저를 찾을 수 없습니다."));

    UserPrincipal principal = UserPrincipal.create(member);

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  @DisplayName("회원 상세정보를 등록 및 수정할 수 있다.")
  void register() {
    // given
    MemberDetailsRequest request = MemberFixture.createDetailsRegisterRequest();

    // when
    MemberDetailsResponse result = memberDetailsService.register(request);

    // then
    assertThat(result.getTimezone()).isEqualTo(9);
    assertThat(result.getMbti()).isEqualTo(MBTI.ENFJ);
    assertThat(result.getAboutMe()).isEqualTo("자기소개입니다");
    assertThat(result.getLocation()).isEqualTo("서울 강남구");
  }

  @Test
  @DisplayName("회원 상세정보를 조회할 수 있다.")
  void getMemberDetails() {
    // given
    MemberDetailsRequest request = MemberFixture.createDetailsReadRequest();
    memberDetailsService.register(request);

    // when
    MemberDetailsResponse result = memberDetailsService.getMemberDetails();

    // then
    assertThat(result.getTimezone()).isEqualTo(8);
    assertThat(result.getMbti()).isEqualTo(MBTI.INFP);
    assertThat(result.getLocation()).isEqualTo("부산 해운대구");
  }

  @Test
  @DisplayName("회원 상세정보를 수정할 수 있다.")
  void editMemberDetails() {
    // given
    MemberDetailsRequest initialRequest = MemberFixture.createDetailsInitialRequest();
    memberDetailsService.register(initialRequest);

    MemberDetailsRequest updateRequest = MemberFixture.createDetailsUpdateRequest();

    // when
    var updated = memberDetailsService.editMemberDetails(updateRequest);

    // then
    assertThat(updated.getTimezone()).isEqualTo(7);
    assertThat(updated.getMbti()).isEqualTo(MBTI.ENTP);
    assertThat(updated.getAboutMe()).isEqualTo("수정된 소개");
    assertThat(updated.getLocation()).isEqualTo("제주도 제주시");
  }

  @Test
  @DisplayName("회원 상세정보를 soft delete 할 수 있다.")
  void deleteMemberDetails() {
    // given
    MemberDetailsRequest request = MemberFixture.createDetailsDeleteRequest();
    memberDetailsService.register(request);

    // when
    var deleted = memberDetailsService.deleteMemberDetails();

    // then
    assertThat(deleted.isDeleted()).isTrue();
  }
}

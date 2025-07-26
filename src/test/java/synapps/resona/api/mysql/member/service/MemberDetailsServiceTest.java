package synapps.resona.api.mysql.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import synapps.resona.api.IntegrationTestSupport;
import synapps.resona.api.mysql.member.dto.request.auth.RegisterRequest;
import synapps.resona.api.mysql.member.dto.request.member_details.MemberDetailsRequest;
import synapps.resona.api.mysql.member.dto.response.MemberDetailsResponse;
import synapps.resona.api.mysql.member.entity.member_details.MBTI;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Language;

@Transactional
class MemberDetailsServiceTest extends IntegrationTestSupport {

  private final String email = "test@resona.com";

  @Autowired
  private MemberService memberService;

  @Autowired
  private MemberDetailsService memberDetailsService;

  @Autowired
  private TempTokenService tempTokenService;

  @BeforeEach
  void setUp() {
    // 1. 회원가입에 사용할 DTO를 준비합니다.
    RegisterRequest request = new RegisterRequest(
        email,
        "test tag",
        "secure123!",
        CountryCode.KR,
        CountryCode.KR,
        new HashSet<>(Set.of(Language.KOREAN)),
        new HashSet<>(Set.of(Language.ENGLISH)),
        9,
        "1998-07-21",
        "테스트닉네임",
        "http://image.png"
    );

    // 2. 임시 회원을 먼저 생성합니다.
    tempTokenService.createTemporaryToken(request.getEmail());

    // 3. 정식 회원으로 전환합니다.
    memberService.signUp(request);

    // 4. 테스트를 위한 인증 정보를 설정합니다.
    User principal = new User(email, "", new ArrayList<>());
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  @DisplayName("회원 상세정보를 등록 및 수정할 수 있다.")
  void register() {
    // given
    MemberDetailsRequest request = new MemberDetailsRequest(
        9, "010-1234-5678", MBTI.ENFJ, "자기소개입니다", "서울 강남구"
    );

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
    MemberDetailsRequest request = new MemberDetailsRequest(
        8, "010-2222-3333", MBTI.INFP, "소개글", "부산 해운대구"
    );
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
    MemberDetailsRequest initialRequest = new MemberDetailsRequest(
        9, "010-1111-2222", MBTI.ENFJ, "초기 소개", "대전 중구"
    );
    memberDetailsService.register(initialRequest);

    MemberDetailsRequest updateRequest = new MemberDetailsRequest(
        7, "010-9999-8888", MBTI.ENTP, "수정된 소개", "제주도 제주시"
    );

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
    MemberDetailsRequest request = new MemberDetailsRequest(
        9, "010-0000-0000", MBTI.ISFJ, "삭제용 소개", "인천 연수구"
    );
    memberDetailsService.register(request);

    // when
    var deleted = memberDetailsService.deleteMemberDetails();

    // then
    assertThat(deleted.isDeleted()).isTrue();
  }
}
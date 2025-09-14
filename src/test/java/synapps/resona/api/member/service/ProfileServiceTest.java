package synapps.resona.api.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import synapps.resona.api.member.dto.request.profile.ProfileRequest;
import synapps.resona.api.member.dto.response.MemberDto;
import synapps.resona.api.member.dto.response.ProfileResponse;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.exception.ProfileException;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Transactional
class ProfileServiceTest extends IntegrationTestSupport {

  private final String email = "test@resona.com";

  private MemberDto memberInfo;
  @Autowired
  private MemberService memberService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private ProfileService profileService;

  @BeforeEach
  void setUp() {
    Member newMember = MemberFixture.createProfileTestMember(email);
    newMember.encodePassword(newMember.getPassword());
    memberRepository.save(newMember);

    RegisterRequest request = MemberFixture.createProfileTestRegisterRequest(email);
    memberService.signUp(request);

    // 인증 정보 설정
    Member member = memberRepository.findByEmailWithAccountInfo(email)
        .orElseThrow(() -> new RuntimeException("테스트 유저를 찾을 수 없습니다."));

    UserPrincipal principal = UserPrincipal.create(member);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    memberInfo = MemberFixture.createTestMemberDto(member.getId(), email);
  }

  @Test
  @DisplayName("프로필 등록에 성공한다.")
  void register() {
    // given
    ProfileRequest request = MemberFixture.createProfileRegisterRequest();

    // when
    ProfileResponse result = profileService.register(request, memberInfo);

    // then
    assertThat(result.getNickname()).isEqualTo("등록된닉네임");
    assertThat(result.getNativeLanguages()).containsExactly("KOREAN");
    assertThat(result.getInterestingLanguages()).contains("ENGLISH");
    assertThat(result.getGender()).isEqualTo("MAN");
    assertThat(result.getComment()).isEqualTo("등록 테스트용 자기소개입니다.");
  }

  @Test
  @DisplayName("등록된 프로필을 조회할 수 있다.")
  void readProfile() {
    // given
    ProfileRequest request = MemberFixture.createProfileReadRequest();
    profileService.register(request, memberInfo);

    // when
    ProfileResponse result = profileService.readProfile(memberInfo);

    // then
    assertThat(result.getNickname()).isEqualTo("조회용닉네임");
    assertThat(result.getGender()).isEqualTo("WOMAN");
    assertThat(result.getComment()).isEqualTo("조회 테스트용 자기소개");
  }

  @Test
  @DisplayName("기존 프로필을 수정할 수 있다.")
  void editProfile() {
    // given
    ProfileRequest initialRequest = MemberFixture.createProfileReadRequest();
    profileService.register(initialRequest, memberInfo);

    ProfileRequest updateRequest = MemberFixture.createProfileUpdateRequest();

    // when
    ProfileResponse result = profileService.editProfile(updateRequest, memberInfo);

    // then
    assertThat(result.getNickname()).isEqualTo("수정된닉네임");
    assertThat(result.getNationality()).isEqualTo("JP");
    assertThat(result.getCountryOfResidence()).isEqualTo("US");
    assertThat(result.getNativeLanguages()).containsExactly("JAPANESE");
    assertThat(result.getInterestingLanguages()).contains("ENGLISH", "FRENCH");
    assertThat(result.getGender()).isEqualTo("MAN");
    assertThat(result.getComment()).isEqualTo("수정된 소개입니다.");
  }

  @Test
  @DisplayName("프로필 삭제 시 softDelete 되어야 하며, 더 이상 조회되지 않아야 한다.")
  void deleteProfile() {
    // given
    ProfileRequest request = MemberFixture.createProfileReadRequest();
    profileService.register(request, memberInfo);

    // when
    profileService.deleteProfile(memberInfo);

    // then
    assertThrows(ProfileException.class, () -> profileService.readProfile(memberInfo));
  }

  @Test
  @DisplayName("이미 등록된 tag 값이 있는 경우 true를 반환한다.")
  void checkDuplicateTag_shouldReturnTrueWhenTagExists() {
    // given
    String existingTag = "test_tag";

    // when
    boolean result = profileService.checkDuplicateTag(existingTag);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("등록되지 않은 tag 값일 경우 false를 반환한다.")
  void checkDuplicateTag_shouldReturnFalseWhenTagDoesNotExist() {
    // given
    String nonExistentTag = "non-existent-tag";

    // when
    boolean result = profileService.checkDuplicateTag(nonExistentTag);

    // then
    assertThat(result).isFalse();
  }
}

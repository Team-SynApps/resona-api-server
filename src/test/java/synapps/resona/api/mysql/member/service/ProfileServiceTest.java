package synapps.resona.api.mysql.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import synapps.resona.api.mysql.member.dto.request.profile.ProfileRequest;
import synapps.resona.api.mysql.member.dto.response.ProfileResponse;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.account.RoleType;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Gender;
import synapps.resona.api.mysql.member.entity.profile.Language;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.member.exception.ProfileException;
import synapps.resona.api.mysql.member.repository.account.AccountInfoRepository;
import synapps.resona.api.mysql.member.repository.member.MemberRepository;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Transactional
class ProfileServiceTest extends IntegrationTestSupport {

  private final String email = "test@resona.com";
  @Autowired
  private MemberService memberService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private AccountInfoRepository accountInfoRepository;
  @Autowired
  private ProfileService profileService;

  @BeforeEach
  void setUp() {
    AccountInfo accountInfo = AccountInfo.of(
        RoleType.GUEST,
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
        "secure123!",
        LocalDateTime.now()
    );

    // AccountInfo 생성

    // 비밀번호 인코딩 및 저장
    newMember.encodePassword(newMember.getPassword());
    memberRepository.save(newMember);
    accountInfoRepository.save(accountInfo);

    RegisterRequest request = new RegisterRequest(
        email,
        "test tag",
        "secure123!",
        CountryCode.KR,
        CountryCode.KR,
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        9,
        "1998-07-21",
        "테스트닉네임",
        "http://image.png"
    );

    memberService.signUp(request);

    Member member = memberRepository.findByEmailWithAccountInfo(email)
        .orElseThrow(() -> new RuntimeException("테스트 유저를 찾을 수 없습니다."));

    UserPrincipal principal = UserPrincipal.create(member);

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Test
  @DisplayName("프로필 등록에 성공한다.")
  void register() {
    ProfileRequest request = new ProfileRequest(
        "등록된닉네임",
        CountryCode.KR,
        CountryCode.KR,
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        "http://new.profile",
        "http://new.bg",
        "1998-07-21",
        Gender.MAN,
        "등록 테스트용 자기소개입니다."
    );

    ProfileResponse result = profileService.register(request);

    assertThat(result.getNickname()).isEqualTo("등록된닉네임");
    assertThat(result.getNativeLanguages()).containsExactly("KOREAN");
    assertThat(result.getInterestingLanguages()).contains("ENGLISH");
    assertThat(result.getGender()).isEqualTo("MAN");
    assertThat(result.getComment()).isEqualTo("등록 테스트용 자기소개입니다.");
  }

  @Test
  @DisplayName("등록된 프로필을 조회할 수 있다.")
  void readProfile() {
    ProfileRequest request = new ProfileRequest(
        "조회용닉네임",
        CountryCode.fromCode("kr"),
        CountryCode.fromCode("kr"),
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        "http://profile.img",
        "http://background.img",
        "1998-07-21",
        Gender.WOMAN,
        "조회 테스트용 자기소개"
    );

    profileService.register(request);

    ProfileResponse result = profileService.readProfile();

    assertThat(result.getNickname()).isEqualTo("조회용닉네임");
    assertThat(result.getGender()).isEqualTo("WOMAN");
    assertThat(result.getComment()).isEqualTo("조회 테스트용 자기소개");
  }

  @Test
  @DisplayName("기존 프로필을 수정할 수 있다.")
  void editProfile() {
    ProfileRequest request = new ProfileRequest(
        "조회용닉네임",
        CountryCode.fromCode("kr"),
        CountryCode.fromCode("kr"),
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        "http://profile.img",
        "http://background.img",
        "1998-07-21",
        Gender.WOMAN,
        "조회 테스트용 자기소개"
    );

    profileService.register(request);

    ProfileRequest updateRequest = new ProfileRequest(
        "수정된닉네임",
        CountryCode.fromCode("jp"),
        CountryCode.fromCode("us"),
        Set.of(Language.JAPANESE),
        Set.of(Language.ENGLISH, Language.FRENCH),
        "http://updated.img",
        "http://updated.bg",
        "1995-12-31",
        Gender.MAN,
        "수정된 소개입니다."
    );

    ProfileResponse result = profileService.editProfile(updateRequest);

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
    ProfileRequest request = new ProfileRequest(
        "삭제닉네임",
        CountryCode.fromCode("kr"),
        CountryCode.fromCode("kr"),
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        "http://profile.img",
        "http://background.img",
        "1998-07-21",
        Gender.WOMAN,
        "삭제자기소개"
    );
    profileService.register(request);

    // when
    profileService.deleteProfile();

    // then
    assertThrows(ProfileException.class, () -> {
      profileService.readProfile();
    });
  }

  @Test
  @DisplayName("이미 등록된 tag 값이 있는 경우 true를 반환한다.")
  void checkDuplicateTag_shouldReturnTrueWhenTagExists() {
    // given: 프로필 등록
    ProfileRequest request = new ProfileRequest(
        "중복확인용닉네임",
        CountryCode.KR,
        CountryCode.KR,
        Set.of(Language.KOREAN),
        Set.of(Language.ENGLISH),
        "http://profile.img",
        "http://background.img",
        "1998-07-21",
        Gender.WOMAN,
        "중복 태그 확인용 프로필"
    );
    ProfileResponse profileResponse = profileService.register(request);

    // when
    boolean result = profileService.checkDuplicateTag(profileResponse.getTag());

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("등록되지 않은 tag 값일 경우 false를 반환한다.")
  void checkDuplicateTag_shouldReturnFalseWhenTagDoesNotExist() {
    // given: 존재하지 않는 tag
    String nonExistentTag = "non-existent-tag";

    // when
    boolean result = profileService.checkDuplicateTag(nonExistentTag);

    // then
    assertThat(result).isFalse();
  }

}
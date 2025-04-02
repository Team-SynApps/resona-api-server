package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
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
import synapps.resona.api.mysql.member.dto.response.ProfileDto;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Gender;
import synapps.resona.api.mysql.member.entity.profile.Language;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ProfileServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ProfileService profileService;

    private final String email = "test@resona.com";

    @BeforeEach
    void setUp() {
        RegisterRequest request = new RegisterRequest(
                email,
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

        User principal = new User(email, "", new ArrayList<>());
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

        ProfileDto result = profileService.register(request);

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

        ProfileDto result = profileService.readProfile();

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

        ProfileDto result = profileService.editProfile(updateRequest);

        assertThat(result.getNickname()).isEqualTo("수정된닉네임");
        assertThat(result.getNationality()).isEqualTo("JP");
        assertThat(result.getCountryOfResidence()).isEqualTo("US");
        assertThat(result.getNativeLanguages()).containsExactly("JAPANESE");
        assertThat(result.getInterestingLanguages()).contains("ENGLISH", "FRENCH");
        assertThat(result.getGender()).isEqualTo("MAN");
        assertThat(result.getComment()).isEqualTo("수정된 소개입니다.");
    }

    @Test
    @DisplayName("프로필 삭제 시 softDelete 되어야 한다.")
    void deleteProfile() {
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

        profileService.deleteProfile();

        ProfileDto result = profileService.readProfile();
        assertThat(result.getNickname()).isEqualTo("삭제닉네임");
    }
}
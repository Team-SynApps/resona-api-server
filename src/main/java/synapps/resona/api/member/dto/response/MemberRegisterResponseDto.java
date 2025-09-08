package synapps.resona.api.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.entity.member_details.MemberDetails;
import synapps.resona.api.member.entity.profile.CountryCode;
import synapps.resona.api.global.entity.Language;
import synapps.resona.api.member.entity.profile.Profile;

@Data
@Builder
public class MemberRegisterResponseDto {

  @Schema(description = "회원 고유 ID", example = "123")
  private Long memberId;

  @Schema(description = "회원 이메일", example = "newuser@example.com")
  private String email;

  @Schema(description = "회원 닉네임", example = "newuser")
  private String nickname;

  @Schema(description = "회원 태그", example = "spculatingwook")
  private String tag;

  @Schema(description = "회원 국적 코드", example = "KR")
  private CountryCode nationality;

  @Schema(description = "회원 거주 국가 코드", example = "US")
  private CountryCode countryOfResidence;

  @Schema(description = "회원의 모국어 목록", example = "[\"ko\", \"en\"]")
  private Set<Language> nativeLanguages;

  @Schema(description = "회원의 관심 언어 목록", example = "[\"ja\", \"fr\"]")
  private Set<Language> interestingLanguages;

  @Schema(description = "회원 타임존", example = "9")
  private Integer timezone;

  @Schema(description = "회원 생년월일 (yyyy-MM-dd)", example = "1990-01-01")
  private String birth;

  @Schema(description = "프로필 이미지 URL", example = "http://example.com/profile.jpg")
  private String profileImageUrl;

  public static MemberRegisterResponseDto from(Member member, Profile profile,
      MemberDetails memberDetails) {
    return MemberRegisterResponseDto.builder()
        .memberId(member.getId())
        .email(member.getEmail())
        .tag(profile.getTag())
        .nationality(profile.getNationality())
        .countryOfResidence(profile.getCountryOfResidence())
        .nativeLanguages(profile.getNativeLanguages())
        .interestingLanguages(profile.getInterestingLanguages())
        .birth(profile.getBirth().toString())
        .nickname(profile.getNickname())
        .profileImageUrl(profile.getProfileImageUrl())
        .timezone(memberDetails.getTimezone())
        .build();
  }
}

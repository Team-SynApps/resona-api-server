package synapps.resona.api.member.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.member.entity.profile.Profile;

@Data
@Builder
public class ProfileResponse {

  private Long id;
  private String tag;
  private String nickname;
  private String nationality;
  private String countryOfResidence;
  private List<String> nativeLanguages;
  private List<String> interestingLanguages;
  private String profileImageUrl;
  private String backgroundImageUrl;
  private String comment;
  private Integer age;
  private String birth;
  private String gender;

  public static ProfileResponse from(Profile profile) {
    return ProfileResponse.builder()
        .id(profile.getId())
        .tag(profile.getTag())
        .nickname(profile.getNickname())
        .nationality(profile.getNationality().toString())
        .countryOfResidence(profile.getCountryOfResidence().toString())
        .nativeLanguages(profile.getNativeLanguages().stream().map((Enum::toString)).toList())
        .interestingLanguages(
            profile.getInterestingLanguages().stream().map((Enum::toString)).toList())
        .profileImageUrl(profile.getProfileImageUrl())
        .backgroundImageUrl(profile.getBackgroundImageUrl())
        .comment(profile.getComment())
        .age(profile.getAge())
        .birth(DateTimeUtil.localDateTimeToString(profile.getBirth()))
        .gender(profile.getGender().toString())
        .build();
  }
}
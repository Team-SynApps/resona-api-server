package com.synapps.resona.common.dto; // 공통 DTO 패키지로 이동하는 것을 고려해보세요.

import com.synapps.resona.command.entity.profile.Profile;
import com.synapps.resona.entity.Language;
import com.synapps.resona.query.entity.MemberDocument;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {
  private Long id;
  private String tag;
  private String nickname;
  private String nationality;
  private String countryOfResidence;
  private List<String> nativeLanguageCodes;
  private List<String> interestingLanguageCodes;
  private String profileImageUrl;
  private String backgroundImageUrl;
  private String comment;
  private Integer age;
  private String birth;
  private String gender;

  private static final DateTimeFormatter BIRTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static ProfileResponse from(Profile profile) {
    String formattedBirth = profile.getBirth() != null
        ? profile.getBirth().format(BIRTH_FORMATTER)
        : null;

    return ProfileResponse.builder()
        .id(profile.getId())
        .tag(profile.getTag())
        .nickname(profile.getNickname())
        .nationality(profile.getNationality().toString())
        .countryOfResidence(profile.getCountryOfResidence().toString())
        .nativeLanguageCodes(profile.getNativeLanguages().stream().map(Language::getCode).toList())
        .interestingLanguageCodes(profile.getInterestingLanguages().stream().map(Language::getCode).toList())
        .profileImageUrl(profile.getProfileImageUrl())
        .backgroundImageUrl(profile.getBackgroundImageUrl())
        .comment(profile.getComment())
        .age(profile.getAge())
        .birth(formattedBirth)
        .gender(profile.getGender().toString())
        .build();
  }

  public static ProfileResponse from(MemberDocument.ProfileEmbed embed) {
    String formattedBirth = embed.getBirth() != null
        ? embed.getBirth().format(BIRTH_FORMATTER)
        : null;

    return ProfileResponse.builder()
        .tag(embed.getTag())
        .nickname(embed.getNickname())
        .nationality(embed.getNationality().toString())
        .countryOfResidence(embed.getCountryOfResidence().toString())
        .nativeLanguageCodes(embed.getNativeLanguages().stream().map(Language::getCode).toList())
        .interestingLanguageCodes(embed.getInterestingLanguages().stream().map(Language::getCode).toList())
        .profileImageUrl(embed.getProfileImageUrl())
        .backgroundImageUrl(embed.getBackgroundImageUrl())
        .comment(embed.getComment())
        .age(embed.getAge())
        .birth(formattedBirth)
        .gender(embed.getGender().toString())
        .build();
  }
}
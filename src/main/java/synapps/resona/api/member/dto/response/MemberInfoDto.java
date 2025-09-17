package synapps.resona.api.member.dto.response;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Data;

import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.member.entity.account.AccountInfo;
import synapps.resona.api.member.entity.member_details.MemberDetails;
import synapps.resona.api.global.entity.Language;
import synapps.resona.api.member.entity.profile.Profile;

@Data
@Builder
public class MemberInfoDto {

  // Account Info
  private String roleType;
  private String accountStatus;
  private String lastAccessedAt;
//  private String providerType;

  // Member Details
  private Integer timezone;
  private String phoneNumber;
  private String location;
  private String mbti;
  private String aboutMe;

  // Profile
  private String nickname;
  private String tag;
  private String nationality;
  private String countryOfResidence;
  private Set<String> nativeLanguages;
  private Set<String> interestingLanguages;
  private String profileImageUrl;
  private String backgroundImageUrl;
  private String birth;
  private Integer age;
  private String gender;
  private String comment;

  public static MemberInfoDto from(AccountInfo accountInfo, MemberDetails memberDetails, Profile profile) {
    return MemberInfoDto.builder()
        .applyAccountInfo(accountInfo)
        .applyMemberDetails(memberDetails)
        .applyProfile(profile)
        .build();
  }

  // Builder 확장 메서드
  public static class MemberInfoDtoBuilder {

    public MemberInfoDtoBuilder applyAccountInfo(AccountInfo info) {
      this.roleType = safeToString(info != null ? info.getRoleType() : null);
      this.accountStatus = safeToString(info != null ? info.getStatus() : null);
      return this;
    }

    public MemberInfoDtoBuilder applyMemberDetails(MemberDetails details) {
      this.timezone = safeToInt(details != null ? details.getTimezone() : null);
      this.phoneNumber = safeToString(details != null ? details.getPhoneNumber() : null);
      this.location = safeToString(details != null ? details.getLocation() : null);
      this.mbti = safeToString(details != null && details.getMbti() != null ? details.getMbti() : null);
      this.aboutMe = safeToString(details != null ? details.getAboutMe() : null);
      return this;
    }

    public MemberInfoDtoBuilder applyProfile(Profile profile) {
      this.nickname = safeToString(profile != null ? profile.getNickname() : null);
      this.tag = safeToString(profile != null ? profile.getTag() : null);
      this.nationality = safeToString(profile != null ? profile.getNationality() : null);
      this.countryOfResidence = safeToString(profile != null ? profile.getCountryOfResidence() : null);
      this.nativeLanguages = safeToStringSet(profile != null ? profile.getNativeLanguages() : null);
      this.interestingLanguages = safeToStringSet(profile != null ? profile.getInterestingLanguages() : null);
      this.profileImageUrl = safeToString(profile != null ? profile.getProfileImageUrl() : null);
      this.backgroundImageUrl = safeToString(profile != null ? profile.getBackgroundImageUrl() : null);
      this.birth = profile != null ? DateTimeUtil.localDateTimeToStringSimpleFormat(profile.getBirth()) : null;
      this.age = safeToInt(profile != null ? profile.getAge() : null);
      this.gender = safeToString(profile != null && profile.getGender() != null ? profile.getGender() : null);
      this.comment = safeToString(profile != null ? profile.getComment() : null);
      return this;
    }
  }

  private static String safeToString(Object obj) {
    return Optional.ofNullable(obj).map(Object::toString).orElse("");
  }

  private static Integer safeToInt(Integer value) {
    return Optional.ofNullable(value).orElse(0);
  }

  private static Set<String> safeToStringSet(Set<Language> languages) {
    return Optional.ofNullable(languages)
        .map(langSet -> langSet.stream()
            .map(Language::getCode)
            .collect(Collectors.toSet()))
        .orElse(Set.of());
  }
}
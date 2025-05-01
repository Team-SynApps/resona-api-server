package synapps.resona.api.mysql.member.dto.response;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import synapps.resona.api.mysql.member.entity.profile.Language;

@Data
@AllArgsConstructor
@Builder
public class MemberInfoDto {

  // Account Info
  private String roleType;
  private String accountStatus;
  private String lastAccessedAt;
  private String providerType;

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
  private Set<Language> nativeLanguages;
  private Set<Language> interestingLanguages;
  private String profileImageUrl;
  private String backgroundImageUrl;
  private String birth;
  private Integer age;
  private String gender;
  private String comment;
}
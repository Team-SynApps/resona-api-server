package synapps.resona.api.member.dto.request.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.member.entity.profile.CountryCode;
import synapps.resona.api.member.entity.profile.Gender;
import synapps.resona.api.member.entity.profile.Language;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {

  @NotBlank
  @Size(max = 15)
  private String nickname;

  @NotNull
  private CountryCode nationality;

  @NotNull
  private CountryCode countryOfResidence;

  @NotNull
  private Set<Language> nativeLanguages;

  @NotNull
  private Set<Language> interestingLanguages;

  @Size(max = 512)
  private String profileImageUrl;

  @Size(max = 512)
  private String backgroundImageUrl;

  @NotNull
  private String birth;

  @NotNull
  private Gender gender;

  @Size(max = 512)
  private String comment;
}
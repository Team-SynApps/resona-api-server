package com.synapps.resona.command.dto.request.profile;

import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.command.entity.profile.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  private Set<String> nativeLanguageCodes;

  @NotNull
  private Set<String> interestingLanguageCodes;

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
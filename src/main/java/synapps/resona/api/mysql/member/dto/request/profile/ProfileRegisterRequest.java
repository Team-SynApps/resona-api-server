package synapps.resona.api.mysql.member.dto.request.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Gender;
import synapps.resona.api.mysql.member.entity.profile.Language;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRegisterRequest {

    @NotNull
    private Long memberId;

    @NotBlank
    @Size(max = 15)
    private String nickname;

    @NotNull
    private String nationality;

    @NotNull
    private String countryOfResidence;

    @NotNull
    private List<String> nativeLanguages;

    @NotNull
    private List<String> interestingLanguages;

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
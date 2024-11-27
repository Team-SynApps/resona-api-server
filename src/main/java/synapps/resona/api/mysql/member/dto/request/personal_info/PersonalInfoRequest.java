package synapps.resona.api.mysql.member.dto.request.personal_info;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.mysql.member.entity.CountryCode;
import synapps.resona.api.mysql.member.entity.personal_info.Gender;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PersonalInfoRequest {

    @NotNull
    private Long memberId; // Member와 매핑할 ID

    private Integer timezone;

    @NotNull
    private CountryCode nationality;

    @NotNull
    private CountryCode countryOfResidence;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotNull
    private LocalDateTime birth;

    @NotNull
    private Gender gender;

    private String location;
}


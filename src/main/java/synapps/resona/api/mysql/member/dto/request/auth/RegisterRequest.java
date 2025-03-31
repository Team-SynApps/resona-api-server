package synapps.resona.api.mysql.member.dto.request.auth;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.mysql.member.entity.profile.CountryCode;
import synapps.resona.api.mysql.member.entity.profile.Language;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    private String password;

    @NotNull
    private CountryCode nationality;

    @NotNull
    private CountryCode countryOfResidence;

    private Set<Language> nativeLanguages;

    private Set<Language> interestingLanguages;

    @NotNull
    private Integer timezone;

    @NotNull
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일은 yyyy-MM-dd 형식이어야 합니다.")
    private String birth = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

    @NotBlank
    @Size(max = 15)
    private String nickname;

    @NotBlank
    private String profileImageUrl;


//
//    @NotBlank
//    @Size(max = 20)
//    private String phoneNumber;
//
//
//    @NotNull
//    private Integer age;
//
//
//    @Size(max = 512)
//    private String comment;
//
//    @NotNull
//    private String sex;

//    @NotNull
//    private String location;
//
//    private String providerType;
//
//    private String category;
//
}
package synapps.resona.api.member.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.member.entity.profile.CountryCode;
import synapps.resona.api.global.entity.Language;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RegisterRequest {

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 30)
  @Pattern(regexp = "^[a-zA-Z_]+$", message = "태그는 영문과 언더스코어(_)만 사용할 수 있습니다.")
  private String tag;

  @Size(max = 120)
  private String password;

  @NotNull
  private CountryCode nationality;

  @NotNull
  private CountryCode countryOfResidence;

  private Set<String> nativeLanguageCodes;

  private Set<String> interestingLanguageCodes;

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

  private boolean isSocialLogin;

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
package synapps.resona.api.mysql.member.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProfileDto {

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
}
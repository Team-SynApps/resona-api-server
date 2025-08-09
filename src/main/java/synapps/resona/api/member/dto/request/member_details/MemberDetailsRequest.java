package synapps.resona.api.member.dto.request.member_details;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.member.entity.member_details.MBTI;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetailsRequest {

  private Integer timezone;

  @NotBlank
  @Size(max = 20)
  private String phoneNumber;

  private MBTI mbti;

  private String aboutMe;

  private String location;
}
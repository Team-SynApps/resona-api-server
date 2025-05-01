package synapps.resona.api.mysql.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MemberDto {

  private Long id;
  private String email;
}
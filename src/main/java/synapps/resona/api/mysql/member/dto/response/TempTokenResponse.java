package synapps.resona.api.mysql.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import synapps.resona.api.mysql.token.AuthToken;

@Data
@AllArgsConstructor
public class TempTokenResponse {

  private AuthToken accessToken;
  private AuthToken refreshToken;
  private boolean isRegistered;
}

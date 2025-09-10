package synapps.resona.api.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import synapps.resona.api.token.AuthToken;

@Data
@AllArgsConstructor
public class TokenResponse {

  private AuthToken accessToken;
  private AuthToken refreshToken;
  private boolean isRegistered;
}

package synapps.resona.api.mysql.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import synapps.resona.api.mysql.token.AuthToken;

@Data
@AllArgsConstructor
public class TokenResponse {
    private AuthToken accessToken;
    private AuthToken refreshToken;
    private boolean isRegistered;
}

package synapps.resona.api.mysql.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String memberEmail;
    private String password;
}

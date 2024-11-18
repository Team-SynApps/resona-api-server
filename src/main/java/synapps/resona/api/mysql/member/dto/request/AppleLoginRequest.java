package synapps.resona.api.mysql.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class AppleLoginRequest {

    @NotBlank(message = "1012:공백일 수 없습니다.")
    private String token;
}

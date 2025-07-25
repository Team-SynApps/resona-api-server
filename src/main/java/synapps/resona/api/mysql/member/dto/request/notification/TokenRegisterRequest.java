package synapps.resona.api.mysql.member.dto.request.notification;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRegisterRequest {

  private Long deviceId;

  private String fcmToken;

  private String deviceInfo;

  private boolean isActive;
}
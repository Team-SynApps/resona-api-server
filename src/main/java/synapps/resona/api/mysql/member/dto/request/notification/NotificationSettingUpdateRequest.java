package synapps.resona.api.mysql.member.dto.request.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "알림 설정 수정 요청 DTO")
public class NotificationSettingUpdateRequest {

    @Schema(description = "친구 요청 알림", example = "true")
    private boolean friendRequestEnabled;

    @Schema(description = "마케팅 알림", example = "false")
    private boolean marketingEnabled;

    @Schema(description = "서비스 알림", example = "true")
    private boolean serviceNotificationEnabled;

    @Schema(description = "소셜 알림", example = "true")
    private boolean socialNotificationEnabled;
}

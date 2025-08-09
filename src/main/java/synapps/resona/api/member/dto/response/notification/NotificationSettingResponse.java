package synapps.resona.api.member.dto.response.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import synapps.resona.api.member.entity.notification.MemberNotificationSetting;

@Getter
@Builder
@Schema(description = "알림 설정 조회 응답 DTO")
public class NotificationSettingResponse {

    @Schema(description = "친구 요청 알림")
    private final boolean friendRequestEnabled;

    @Schema(description = "마케팅 알림")
    private final boolean marketingEnabled;

    @Schema(description = "서비스 알림")
    private final boolean serviceNotificationEnabled;

    @Schema(description = "소셜 알림")
    private final boolean socialNotificationEnabled;

    public static NotificationSettingResponse to(
        MemberNotificationSetting memberNotificationSetting) {
        return NotificationSettingResponse.builder()
            .friendRequestEnabled(memberNotificationSetting.isFriendRequestEnabled())
            .marketingEnabled(memberNotificationSetting.isMarketingEnabled())
            .serviceNotificationEnabled(memberNotificationSetting.isServiceNotificationEnabled())
            .socialNotificationEnabled(memberNotificationSetting.isSocialNotificationEnabled())
            .build();
    }
}

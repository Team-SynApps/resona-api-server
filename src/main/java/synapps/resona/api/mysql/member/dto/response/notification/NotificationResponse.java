package synapps.resona.api.mysql.member.dto.response.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import synapps.resona.api.mysql.member.entity.notification.MemberNotification;

@Getter
@Builder
@Schema(description = "알림 목록 조회 응답 DTO")
public class NotificationResponse {

    @Schema(description = "알림 ID")
    private final Long notificationId;

    @Schema(description = "알림 제목")
    private final String title;

    @Schema(description = "알림 내용")
    private final String body;

    @Schema(description = "알림 이미지")
    private final String image;

    @Schema(description = "알림 아이콘")
    private final String icon;

    @Schema(description = "읽음 여부")
    private final boolean isRead;

    @Schema(description = "생성일")
    private final LocalDateTime createdAt;

    public static NotificationResponse to(MemberNotification notification) {
        return NotificationResponse.builder()
            .notificationId(notification.getId())
            .title(notification.getTitle())
            .body(notification.getBody())
            .image(notification.getImage())
            .icon(notification.getIcon())
            .isRead(notification.isRead())
            .createdAt(notification.getCreatedAt())
            .build();
    }
}

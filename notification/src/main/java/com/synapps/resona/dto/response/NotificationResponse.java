package com.synapps.resona.dto.response;

import com.synapps.resona.entity.MemberNotification;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {

    private final Long notificationId;

    private final String title;

    private final String body;

    private final String image;

    private final String icon;

    private final boolean isRead;

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

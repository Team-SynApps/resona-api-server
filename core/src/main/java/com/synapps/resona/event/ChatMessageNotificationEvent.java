package com.synapps.resona.event;

import com.synapps.resona.entity.MessageType;
import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageNotificationEvent(
    String msgId,
    Long roomId,
    MessageType type,
    String content,
    LocalDateTime timestamp,
    Long senderId,
    String senderNickname,
    String senderProfileImageUrl,
    List<Long> recipientIds
) {
}

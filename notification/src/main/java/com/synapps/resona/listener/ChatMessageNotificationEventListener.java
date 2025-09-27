package com.synapps.resona.listener;

import com.synapps.resona.event.ChatMessageNotificationEvent;
import com.synapps.resona.service.NotificationSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageNotificationEventListener {

    private final NotificationSendService notificationSendService;

    @EventListener
    public void handleChatMessageNotificationEvent(ChatMessageNotificationEvent event) {
        notificationSendService.sendPushForMessage(
            event.roomId(),
            event.senderId(),
            event.senderNickname(),
            event.content(),
            event.recipientIds()
        );
    }
}

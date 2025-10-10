package com.synapps.resona.consumer;

import static com.synapps.resona.config.database.RabbitMQConfig.PUSH_QUEUE_NAME;

import com.synapps.resona.event.ChatMessageNotificationEvent;
import com.synapps.resona.service.NotificationSendService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ로부터 채팅 메시지를 수신하여 푸시 알림 발송 로직을 트리거하는 컨슈머
 */
@Component
@RequiredArgsConstructor
public class PushNotificationConsumer {

  private final NotificationSendService notificationSendService;

  private static final Logger logger = LoggerFactory.getLogger(PushNotificationConsumer.class);

  /**
   * PUSH_QUEUE_NAME 큐를 구독(Listen)하고 있다가, 메시지가 들어오면 이 메서드를 실행
   * @param event RabbitMQ로부터 받은 역직렬화된 메시지 객체
   */
  @RabbitListener(queues = PUSH_QUEUE_NAME)
  public void handleChatMessage(ChatMessageNotificationEvent event) {
    logger.info("Received a chat message for push notification [msgId={}]", event.msgId());
    try {
      // 실제 푸시 발송 로직은 NotificationSendService에 위임
      notificationSendService.sendPushForMessage(
          event.roomId(),
          event.senderId(),
          event.senderNickname(),
          event.content(),
          event.recipientIds()
      );
    } catch (Exception e) {
      logger.error("Failed to process push notification for message [msgId={}]", event.msgId(), e);
      // TODO: 메시지 처리 실패 시 재시도(Retry) 또는 데드 레터 큐(DLQ)로 보내는 에러 핸들링 정책을 구현해야 함
    }
  }
}
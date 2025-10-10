package com.synapps.resona.service;

import static com.synapps.resona.config.database.RabbitMQConfig.CHAT_EXCHANGE_NAME;

import com.synapps.resona.entity.ChatMember;
import com.synapps.resona.entity.ChatMessage;
import com.synapps.resona.entity.ChatRoom;
import com.synapps.resona.event.ChatMessageNotificationEvent;
import com.synapps.resona.repository.RoomRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessagePublisher {
  private final Logger logger = LoggerFactory.getLogger(ChatMessagePublisher.class);

  private final RabbitTemplate rabbitTemplate;
  private final RoomRepository roomRepository;

  private static final String ROUTING_KEY_PREFIX = "room.";

  public void publish(ChatMessage message, ChatMember sender, Long roomId) {
    List<Long> recipientIds = roomRepository.findById(roomId)
        .map(ChatRoom::getMemberIds)
        .orElse(Collections.emptyList());

    ChatMessageNotificationEvent event = new ChatMessageNotificationEvent(
        message.getMsgId().toString(),
        roomId,
        message.getType(),
        message.getContent(),
        message.getTimestamp(),
        sender.getId(),
        sender.getNickname(),
        sender.getProfileImageUrl(),
        recipientIds
    );

    String routingKey = ROUTING_KEY_PREFIX + roomId;

    try {
      rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, routingKey, event);
      logger.info("Message published to exchange '{}' with routing key '{}'. Message: {}", CHAT_EXCHANGE_NAME, routingKey, event);
    } catch (Exception e) {
      logger.error("Failed to publish message to RabbitMQ. Exchange: {}, RoutingKey: {}. Error: {}", CHAT_EXCHANGE_NAME, routingKey, e.getMessage());
    }
  }
}
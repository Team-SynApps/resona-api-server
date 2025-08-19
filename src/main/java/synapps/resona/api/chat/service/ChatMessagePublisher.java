package synapps.resona.api.chat.service;

import static synapps.resona.api.global.config.database.RabbitMQConfig.CHAT_EXCHANGE_NAME;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import synapps.resona.api.chat.dto.MessageDto;
import synapps.resona.api.chat.entity.ChatMessage;
import synapps.resona.api.chat.entity.ChatMember;
import synapps.resona.api.chat.exception.ChatException;
import synapps.resona.api.chat.repository.ChatMemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessagePublisher {

  private final RabbitTemplate rabbitTemplate;

  private static final String ROUTING_KEY_PREFIX = "room.";

  public void publish(ChatMessage message, ChatMember sender, Long roomId) {
    MessageDto messageDto = MessageDto.of(message, sender, roomId);

    String routingKey = ROUTING_KEY_PREFIX + roomId;

    try {
      rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, routingKey, messageDto);
      log.info("Message published to exchange '{}' with routing key '{}'. Message: {}", CHAT_EXCHANGE_NAME, routingKey, messageDto);
    } catch (Exception e) {
      log.error("Failed to publish message to RabbitMQ. Exchange: {}, RoutingKey: {}. Error: {}", CHAT_EXCHANGE_NAME, routingKey, e.getMessage());
    }
  }
}
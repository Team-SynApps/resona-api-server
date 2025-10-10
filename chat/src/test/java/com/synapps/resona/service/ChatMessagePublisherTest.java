package com.synapps.resona.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.synapps.resona.dto.MessageDto;
import com.synapps.resona.entity.ChatMessage;
import com.synapps.resona.entity.ChatMember;
import com.synapps.resona.entity.ChatRoom;
import com.synapps.resona.event.ChatMessageNotificationEvent;
import com.synapps.resona.repository.RoomRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class ChatMessagePublisherTest {

  private static final String CHAT_EXCHANGE_NAME = "chat.exchange";

  @InjectMocks
  private ChatMessagePublisher chatMessagePublisher;

  @Mock
  private RabbitTemplate rabbitTemplate;

  @Mock
  private RoomRepository roomRepository;

  @Nested
  @DisplayName("publish 메서드 테스트")
  class PublishTests {

    @Test
    @DisplayName("메시지를 성공적으로 발행한다.")
    void shouldPublishMessageSuccessfully() {
      // given
      Long roomId = 100L;
      Long senderId = 1L;
      ChatMember sender = ChatMember.of(senderId, "테스트유저", "profile.jpg");
      ChatMessage chatMessage = ChatMessage.createTextMessage(senderId, "안녕하세요", LocalDateTime.now());
      ChatRoom chatRoom = ChatRoom.of(roomId, "Test Room", List.of(senderId, 2L, 3L));
      when(roomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));

      // when
      chatMessagePublisher.publish(chatMessage, sender, roomId);

      // then
      ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
      ArgumentCaptor<ChatMessageNotificationEvent> messageDtoCaptor = ArgumentCaptor.forClass(ChatMessageNotificationEvent.class);

      // rabbitTemplate.convertAndSend 메서드가 정확히 1번 호출되었는지 검증
      verify(rabbitTemplate, times(1))
          .convertAndSend(exchangeCaptor.capture(), routingKeyCaptor.capture(), messageDtoCaptor.capture());

      // 캡처된 인자들이 예상과 일치하는지 확인
      assertThat(exchangeCaptor.getValue()).isEqualTo(CHAT_EXCHANGE_NAME);
      assertThat(routingKeyCaptor.getValue()).isEqualTo("room." + roomId);

      ChatMessageNotificationEvent capturedEvent = messageDtoCaptor.getValue();
      assertThat(capturedEvent.roomId()).isEqualTo(roomId);
      assertThat(capturedEvent.content()).isEqualTo("안녕하세요");
      assertThat(capturedEvent.senderId()).isEqualTo(senderId);
      assertThat(capturedEvent.senderNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("메시지 발행 중 예외가 발생해도 외부로 전파하지 않는다.")
    void shouldNotPropagateExceptionOnPublishFailure() {
      // given
      Long roomId = 101L;
      ChatMember sender = ChatMember.of(2L, "에러유저", "error.jpg");
      ChatMessage chatMessage = ChatMessage.createTextMessage(2L, "에러 메시지", LocalDateTime.now());

      doThrow(new AmqpException("RabbitMQ connection failed"))
          .when(rabbitTemplate)
          .convertAndSend(any(String.class), any(String.class), any(MessageDto.class));

      // when & then
      // publish 메서드 실행 시 어떤 예외도 발생하지 않음을 검증
      assertThatCode(() -> chatMessagePublisher.publish(chatMessage, sender, roomId))
          .doesNotThrowAnyException();

      // 예외가 발생했더라도, 메시지 전송 시도는 1번 이루어졌는지 확인
      verify(rabbitTemplate, times(1)).convertAndSend(any(String.class), any(String.class), any(ChatMessageNotificationEvent.class));
    }
  }
}
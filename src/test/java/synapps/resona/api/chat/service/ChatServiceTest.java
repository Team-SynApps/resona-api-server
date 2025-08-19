package synapps.resona.api.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import synapps.resona.api.chat.dto.MessageDto;
import synapps.resona.api.chat.dto.SendMessageRequest;
import synapps.resona.api.chat.entity.ChatMember;
import synapps.resona.api.chat.entity.ChatMessage;
import synapps.resona.api.chat.entity.MessageBucket;
import synapps.resona.api.chat.exception.ChatException;
import synapps.resona.api.chat.repository.ChatMemberRepository;
import synapps.resona.api.chat.repository.MessageBucketRepository;
import synapps.resona.api.chat.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  @InjectMocks
  private ChatService chatService;

  @Mock
  private MessageBucketRepository messageBucketRepository;
  @Mock
  private RoomRepository roomRepository;
  @Mock
  private ChatMemberRepository chatMemberRepository;

  @Nested
  @DisplayName("sendMessage 메서드 테스트")
  class SendMessageTests {

    @Test
    @DisplayName("기존 버킷에 메시지를 추가할 수 있다.")
    void shouldAddMessageToExistingBucket() {
      // given
      Long memberId = 1L;
      Long roomId = 100L;
      SendMessageRequest request = new SendMessageRequest("안녕하세요");

      ChatMessage firstMessage = ChatMessage.createTextMessage(memberId, "첫 메시지", LocalDateTime.now());
      MessageBucket existingBucket = MessageBucket.createFirstBucket(roomId, 1, firstMessage);

      given(roomRepository.isMemberInRoom(roomId, memberId)).willReturn(true);
      given(messageBucketRepository.findLatestAvailableBucket(anyLong(), anyInt())).willReturn(Optional.of(existingBucket));

      // when
      chatService.sendMessage(memberId, roomId, request);

      // then
      verify(messageBucketRepository, times(1)).save(any(MessageBucket.class));

      ArgumentCaptor<MessageBucket> bucketCaptor = ArgumentCaptor.forClass(MessageBucket.class);
      verify(messageBucketRepository).save(bucketCaptor.capture());

      MessageBucket savedBucket = bucketCaptor.getValue();
      assertThat(savedBucket.getMessageCount()).isEqualTo(2);
      assertThat(savedBucket.getMessages().get(1).getContent()).isEqualTo("안녕하세요");
    }

    @Test
    @DisplayName("새 버킷을 생성하여 메시지를 추가할 수 있다.")
    void shouldCreateNewBucketAndAddMessage() {
      // given
      Long memberId = 1L;
      Long roomId = 100L;
      SendMessageRequest request = new SendMessageRequest("새 버킷 메시지");

      given(roomRepository.isMemberInRoom(roomId, memberId)).willReturn(true);
      given(messageBucketRepository.findLatestAvailableBucket(anyLong(), anyInt())).willReturn(Optional.empty());
      given(messageBucketRepository.findLastBucket(roomId)).willReturn(Optional.empty());

      // when
      chatService.sendMessage(memberId, roomId, request);

      // then
      verify(messageBucketRepository, times(1)).save(any(MessageBucket.class));

      ArgumentCaptor<MessageBucket> bucketCaptor = ArgumentCaptor.forClass(MessageBucket.class);
      verify(messageBucketRepository).save(bucketCaptor.capture());

      MessageBucket savedBucket = bucketCaptor.getValue();
      assertThat(savedBucket.getBucketSequence()).isEqualTo(1);
      assertThat(savedBucket.getMessageCount()).isEqualTo(1);
      assertThat(savedBucket.getMessages().get(0).getContent()).isEqualTo("새 버킷 메시지");
    }

    @Test
    @DisplayName("채팅방 멤버가 아니면 예외를 발생시킨다.")
    void shouldThrowExceptionIfNotAMember() {
      // given
      Long memberId = 1L;
      Long roomId = 100L;
      SendMessageRequest request = new SendMessageRequest("안녕하세요");

      given(roomRepository.isMemberInRoom(roomId, memberId)).willReturn(false);

      // when & then
      assertThatThrownBy(() -> chatService.sendMessage(memberId, roomId, request))
          .isInstanceOf(ChatException.class)
          .hasMessageContaining("Not a member");

      verify(messageBucketRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("getMessages 메서드 테스트")
  class GetMessagesTests {

    @Test
    @DisplayName("메시지 목록과 사용자 정보를 함께 조회할 수 있다.")
    void shouldGetMessagesWithSenderInfo() {
      // given
      Long roomId = 100L;
      LocalDateTime cursor = LocalDateTime.now();
      Long sender1Id = 1L;
      Long sender2Id = 2L;

      ChatMessage msg1 = ChatMessage.createTextMessage(sender1Id, "메시지 1", LocalDateTime.now());
      ChatMessage msg2 = ChatMessage.createTextMessage(sender2Id, "메시지 2", LocalDateTime.now());
      MessageBucket bucket = MessageBucket.createFirstBucket(roomId, 1, msg1);
      bucket.addMessage(msg2);

      ChatMember sender1 = ChatMember.of(sender1Id, "사용자1", "url1");
      ChatMember sender2 = ChatMember.of(sender2Id, "사용자2", "url2");

      Slice<MessageBucket> bucketSlice = new SliceImpl<>(List.of(bucket));
      given(messageBucketRepository.findMessagesBefore(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
          .willReturn(bucketSlice);
      given(chatMemberRepository.findAllByIdIn(Set.of(sender1Id, sender2Id)))
          .willReturn(List.of(sender1, sender2));

      // when
      Slice<MessageDto> result = chatService.getMessages(roomId, cursor);

      // then
      assertThat(result.getContent()).hasSize(2);
      assertThat(result.getContent().get(0).sender().nickname()).isEqualTo("사용자2");
      assertThat(result.getContent().get(1).content()).isEqualTo("메시지 1");
    }

    @Test
    @DisplayName("발신자 정보를 찾을 수 없으면 예외를 발생시킨다.")
    void shouldThrowExceptionIfSenderNotFound() {
      // given
      Long roomId = 100L;
      Long senderId = 1L;

      MessageBucket bucket = MessageBucket.createFirstBucket(roomId, 1, ChatMessage.createTextMessage(senderId, "메시지", LocalDateTime.now()));
      Slice<MessageBucket> bucketSlice = new SliceImpl<>(List.of(bucket));

      given(messageBucketRepository.findMessagesBefore(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
          .willReturn(bucketSlice);
      given(chatMemberRepository.findAllByIdIn(any())).willReturn(List.of());

      // when & then
      assertThatThrownBy(() -> chatService.getMessages(roomId, LocalDateTime.now()))
          .isInstanceOf(ChatException.class)
          .hasMessageContaining("Sender information not found");
    }
  }
}
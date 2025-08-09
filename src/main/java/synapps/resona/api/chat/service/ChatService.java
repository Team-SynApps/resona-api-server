package synapps.resona.api.chat.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.chat.dto.MessageDto;
import synapps.resona.api.chat.dto.SendMessageRequest;
import synapps.resona.api.chat.entity.*;
import synapps.resona.api.chat.exception.ChatException;
import synapps.resona.api.chat.repository.*;

@Service
@RequiredArgsConstructor
public class ChatService {

  private static final int BUCKET_MAX_SIZE = 200;
  private static final int PAGE_SIZE = 30; // 한 페이지에 가져올 메시지 개수

  private final MessageBucketRepository messageBucketRepository;
  private final RoomRepository roomRepository;
  private final ChatMemberRepository chatMemberRepository;

  // TODO: 추후 메시지 발행 로직 구현해야 함(RabbitMQ)
  @Transactional
  public void sendMessage(Long currentMemberId, Long roomId, SendMessageRequest request) {
    // 유저가 채팅방 멤버인지 확인
    if (!roomRepository.isMemberInRoom(roomId, currentMemberId)) {
      throw ChatException.notMemberInRoom();
    }

    // 메시지 생성
    ChatMessage chatMessage = ChatMessage.createTextMessage(currentMemberId, request.getContent());

    // 메시지를 추가할 버킷을 찾거나 새로 생성
    messageBucketRepository.findLatestAvailableBucket(roomId, BUCKET_MAX_SIZE)
        .ifPresentOrElse(
            // 기존 버킷에 메시지 추가
            bucket -> {
              bucket.addMessage(chatMessage);
              messageBucketRepository.save(bucket);
            },
            // 새 버킷 생성
            () -> {
              int nextSequence = messageBucketRepository.findLastBucket(roomId)
                  .map(lastBucket -> lastBucket.getBucketSequence() + 1)
                  .orElse(1);
              MessageBucket newBucket = MessageBucket.createFirstBucket(roomId, nextSequence, chatMessage);
              messageBucketRepository.save(newBucket);
            }
        );
    // TODO: (추후 구현) 메시지 발행 로직 (RabbitMQ)
  }

  @Transactional(readOnly = true)
  public Slice<MessageDto> getMessages(Long roomId, LocalDateTime cursor) {
    // Pageable 객체에 정렬 기준을 명시적으로 포함
    PageRequest pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "startTime"));

    Slice<MessageBucket> buckets = messageBucketRepository.findMessagesBefore(roomId, cursor, pageable);

    List<ChatMessage> messages = buckets.getContent().stream()
        .flatMap(bucket -> bucket.getMessages().stream())
        .toList();

    if (messages.isEmpty()) {
      return new SliceImpl<>(List.of()); // 비어있으면 빈 슬라이스 반환
    }

    var senderIds = messages.stream()
        .map(ChatMessage::getSenderId)
        .collect(Collectors.toSet());

    Map<Long, ChatMember> memberMap = chatMemberRepository.findAllByIdIn(senderIds).stream()
        .collect(Collectors.toMap(ChatMember::getId, member -> member));

    List<MessageDto> messageDtos = messages.stream()
        .map(message -> {
          ChatMember sender = memberMap.get(message.getSenderId());
          if (sender == null) {
            throw ChatException.senderNotFound();
          }
          return MessageDto.of(message, sender);
        })
        .sorted((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()))
        .toList();

    return new SliceImpl<>(messageDtos, pageable, buckets.hasNext());
  }
}
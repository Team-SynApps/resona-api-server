package synapps.resona.api.matching.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.chat.entity.ChatRoom;
import synapps.resona.api.chat.service.ChatMessagePublisher;
import synapps.resona.api.chat.service.ChatRoomService;
import synapps.resona.api.matching.dto.MatchResult;
import synapps.resona.api.matching.exception.MatchingException;
import synapps.resona.api.matching.strategy.MatchingStrategy;

@Service
@RequiredArgsConstructor
public class MatchingService {
  private final Logger logger = LogManager.getLogger(MatchingService.class);

  private final ChatRoomService chatRoomService;
  private final MatchingStrategy matchingStrategy;

  @Transactional
  public ChatRoom processMatchAndCreateRoom(Long requesterId) {
    MatchResult result = matchingStrategy.match(requesterId);

    if (!result.isSuccess() || result.matchedMemberIds().size() < 2) {
      throw MatchingException.matchFailed();
    }

    String roomName = "새로운 채팅방";

    ChatRoom createdRoom = chatRoomService.createChatRoom(
        requesterId,
        roomName,
        result.matchedMemberIds()
    );

    logger.info("자동으로 채팅방이 생성되었습니다. Room ID: {}", createdRoom.getId());

    return createdRoom;
  }
}
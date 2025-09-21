package synapps.resona.api.notification.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.chat.entity.ChatRoom;
import synapps.resona.api.chat.repository.RoomRepository;
import synapps.resona.api.notification.entity.MemberPushToken;
import synapps.resona.api.member.repository.notification.MemberPushTokenRepository;
import synapps.resona.api.notification.dto.MessageDto;

/**
 * 실제 푸시 알림 발송 로직을 전담하는 서비스
 * Redis 상태 조회, 대상 필터링, FCM 발송 요청 등의 책임
 */
@Service
@RequiredArgsConstructor
public class NotificationSendService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final RoomRepository roomRepository;
  private final MemberPushTokenRepository pushTokenRepository;
  private final MemberNotificationService memberNotificationService;

  private static final String ONLINE_USERS_KEY = "online_users";
  private static final String CURRENT_ROOM_KEY_PREFIX = "user:";
  private static final String CURRENT_ROOM_KEY_SUFFIX = ":current_room";

  private static final Logger logger = LoggerFactory.getLogger(NotificationSendService.class);

  @Transactional(readOnly = true) // DB 조회만 수행하므로 readOnly
  public void sendPushForMessage(MessageDto messageDto) {
    // 채팅방 정보를 조회하여 멤버 목록을 가져옴
    ChatRoom room = roomRepository.findById(messageDto.roomId()).orElse(null);
    if (room == null) {
      logger.warn("Push notification failed: Room not found for roomId={}", messageDto.roomId());
      return;
    }

    // 각 멤버에 대해 푸시를 보낼지 결정
    room.getMemberIds().forEach(recipientId -> {
      if (shouldSendPushTo(recipientId, messageDto)) {
        sendFcmNotification(recipientId, messageDto);
      }
    });
  }

  private boolean shouldSendPushTo(Long recipientId, MessageDto messageDto) {
    // 메시지 발신자 자신에게는 보내지 않음
    if (recipientId.equals(messageDto.sender().id())) {
      return false;
    }

    // Redis를 조회하여 수신자의 온라인 상태 확인
    Boolean isOnline = redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, String.valueOf(recipientId));

    // 오프라인 상태이면 무조건 푸시
    if (isOnline == null || !isOnline) {
      return true;
    }

    // 온라인 상태이면, 현재 접속 중인 방을 확인
    String currentRoomKey = CURRENT_ROOM_KEY_PREFIX + recipientId + CURRENT_ROOM_KEY_SUFFIX;
    Object currentRoomIdObj = redisTemplate.opsForValue().get(currentRoomKey);
    String currentRoomId = (currentRoomIdObj != null) ? String.valueOf(currentRoomIdObj) : null;

    // 현재 보고 있는 방이 메시지가 온 방과 같으면 푸시를 보내지 않음
    boolean isInSameRoom = String.valueOf(messageDto.roomId()).equals(currentRoomId);
    return !isInSameRoom;
  }

  private void sendFcmNotification(Long recipientId, MessageDto messageDto) {
    // 수신자의 모든 활성화된 디바이스 토큰을 DB에서 조회
    List<MemberPushToken> tokens = pushTokenRepository.findActiveTokensByMemberId(recipientId);

    if (tokens.isEmpty()) {
      logger.info("No active push tokens found for memberId={}", recipientId);
      return;
    }

    // 각 토큰에 대해 FCM 메시지 발송을 위임
    String title = messageDto.sender().nickname();
    String body = messageDto.content();

    tokens.forEach(token -> {
      try {
        logger.info("Sending push to memberId={} with token={}", recipientId, token.getFcmToken());
        memberNotificationService.sendMessage(token.getFcmToken(), title, body);
      } catch (Exception e) {
        // TODO: FirebaseMessagingException을 구체적으로 잡아서, 만료된 토큰(UNREGISTERED)일 경우 DB에서 삭제하는 로직 추가
        logger.error("Failed to send FCM message to token: {}", token.getFcmToken(), e);
      }
    });
  }
}
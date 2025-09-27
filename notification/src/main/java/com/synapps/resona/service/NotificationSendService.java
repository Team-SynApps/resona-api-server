package com.synapps.resona.service;



import com.synapps.resona.entity.MemberPushToken;

import com.synapps.resona.repository.MemberPushTokenRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 실제 푸시 알림 발송 로직을 전담하는 서비스
 * Redis 상태 조회, 대상 필터링, FCM 발송 요청 등의 책임
 */
@Service
@RequiredArgsConstructor
public class NotificationSendService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final MemberPushTokenRepository pushTokenRepository;
  private final MemberNotificationService memberNotificationService;

  private static final String ONLINE_USERS_KEY = "online_users";
  private static final String CURRENT_ROOM_KEY_PREFIX = "user:";
  private static final String CURRENT_ROOM_KEY_SUFFIX = ":current_room";

  private static final Logger logger = LoggerFactory.getLogger(NotificationSendService.class);

  @Transactional(readOnly = true)
  public void sendPushForMessage(Long roomId, Long senderId, String senderNickname, String content, List<Long> recipientIds) {
    // 각 멤버에 대해 푸시를 보낼지 결정
    recipientIds.forEach(recipientId -> {
      if (shouldSendPushTo(recipientId, roomId, senderId)) {
        sendFcmNotification(recipientId, roomId, senderNickname, content);
      }
    });
  }

  private boolean shouldSendPushTo(Long recipientId, Long roomId, Long senderId) {
    if (recipientId.equals(senderId)) {
      return false;
    }

    Boolean isOnline = redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, String.valueOf(recipientId));

    if (isOnline == null || !isOnline) {
      return true;
    }

    String currentRoomKey = CURRENT_ROOM_KEY_PREFIX + recipientId + CURRENT_ROOM_KEY_SUFFIX;
    Object currentRoomIdObj = redisTemplate.opsForValue().get(currentRoomKey);
    String currentRoomId = (currentRoomIdObj != null) ? String.valueOf(currentRoomIdObj) : null;

    boolean isInSameRoom = String.valueOf(roomId).equals(currentRoomId);
    return !isInSameRoom;
  }

  private void sendFcmNotification(Long recipientId, Long roomId, String senderNickname, String content) {
    List<MemberPushToken> tokens = pushTokenRepository.findActiveTokensByMemberId(recipientId);

    if (tokens.isEmpty()) {
      logger.info("No active push tokens found for memberId={}", recipientId);
      return;
    }

    String title = senderNickname;
    String body = content;

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
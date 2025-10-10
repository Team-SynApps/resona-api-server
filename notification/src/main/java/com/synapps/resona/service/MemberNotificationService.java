package com.synapps.resona.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.synapps.resona.code.NotificationErrorCode;
import com.synapps.resona.dto.request.NotificationSettingUpdateRequest;
import com.synapps.resona.dto.request.TokenRegisterRequest;
import com.synapps.resona.dto.response.NotificationResponse;
import com.synapps.resona.dto.response.NotificationSettingResponse;
import com.synapps.resona.entity.MemberNotification;
import com.synapps.resona.entity.MemberNotificationSetting;
import com.synapps.resona.entity.MemberPushToken;
import com.synapps.resona.entity.NotificationMember;
import com.synapps.resona.exception.NotificationException;
import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.repository.MemberNotificationRepository;
import com.synapps.resona.repository.MemberNotificationSettingRepository;
import com.synapps.resona.repository.MemberPushTokenRepository;
import com.synapps.resona.repository.NotificationMemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberNotificationService {

  private static final Logger logger = LoggerFactory.getLogger(MemberNotificationService.class);

  private final MemberNotificationRepository notificationRepository;
  private final MemberNotificationSettingRepository notificationSettingRepository;
  private final MemberPushTokenRepository pushTokenRepository;
  private final NotificationMemberRepository notificationMemberRepository;


  @Transactional
  public String registerToken(TokenRegisterRequest tokenRegisterRequest, Long memberId) {

    NotificationMember member = notificationMemberRepository.findById(memberId)
        .orElseThrow(() -> NotificationException.of(NotificationErrorCode.MEMBER_NOT_FOUND));

    MemberPushToken pushToken = MemberPushToken.of(
        tokenRegisterRequest.getDeviceId(),
        member,
        tokenRegisterRequest.getFcmToken(),
        tokenRegisterRequest.getDeviceInfo(),
        tokenRegisterRequest.isActive());
    pushTokenRepository.save(pushToken);
    return "register success";
  }

  @Transactional(readOnly = true)
  public NotificationSettingResponse getNotificationSetting(Long memberId) {

    NotificationMember member = notificationMemberRepository.findById(memberId)
        .orElseThrow(() -> NotificationException.of(NotificationErrorCode.MEMBER_NOT_FOUND));

    MemberNotificationSetting setting = notificationSettingRepository.findByMember(member)
        .orElseThrow(NotificationException::notificationSettingNotFound);

    return NotificationSettingResponse.to(setting);
  }

  @Transactional
  public void updateNotificationSetting(NotificationSettingUpdateRequest request, Long memberId) {

    NotificationMember member = notificationMemberRepository.findById(memberId)
        .orElseThrow(() -> NotificationException.of(NotificationErrorCode.MEMBER_NOT_FOUND));

    MemberNotificationSetting setting = notificationSettingRepository.findByMember(member)
        .orElseThrow(NotificationException::notificationSettingNotFound);

    setting.update(request.isFriendRequestEnabled(), request.isMarketingEnabled(),
        request.isServiceNotificationEnabled(), request.isSocialNotificationEnabled());
  }

  @Transactional(readOnly = true)
  public CursorResult<NotificationResponse> getNotifications(Long cursorId, Integer size, Long memberId) {

    NotificationMember member = notificationMemberRepository.findById(memberId)
        .orElseThrow(() -> NotificationException.of(NotificationErrorCode.MEMBER_NOT_FOUND));

    List<MemberNotification> notifications = notificationRepository.findByMemberAndIdLessThanOrderByIdDesc(member, cursorId, PageRequest.of(0, size));
    List<NotificationResponse> notificationResponses = notifications.stream()
        .map(NotificationResponse::to)
        .toList();

    Long lastId = notifications.isEmpty() ? -1L : notifications.get(notifications.size() - 1).getId();
    return new CursorResult<>(notificationResponses, lastId != -1L, String.valueOf(lastId));
  }

  @Transactional
  public void readNotification(Long notificationId, Long memberId) {

    MemberNotification notification = notificationRepository.findById(notificationId)
        .orElseThrow(NotificationException::notificationNotFound);

    if (!notification.getMember().getId().equals(memberId)) {
        throw NotificationException.of(NotificationErrorCode.FORBIDDEN);
    }

    notification.read();
  }

  @Transactional
  public void deleteNotification(Long notificationId, Long memberId) {

    MemberNotification notification = notificationRepository.findById(notificationId)
        .orElseThrow(NotificationException::notificationNotFound);

    if (!notification.getMember().getId().equals(memberId)) {
        throw NotificationException.of(NotificationErrorCode.FORBIDDEN);
    }

    notification.softDelete();
  }

  public void sendMessage(String token, String title, String body) throws FirebaseMessagingException {
    String message = FirebaseMessaging.getInstance().send(Message.builder()
        .setNotification(Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build())
        .setToken(token)
        .build());

    logger.info("Sent message: {}", message);
  }
}

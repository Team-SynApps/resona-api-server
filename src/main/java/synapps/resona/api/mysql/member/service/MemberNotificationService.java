package synapps.resona.api.mysql.member.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.mysql.member.dto.request.notification.NotificationSettingUpdateRequest;
import synapps.resona.api.mysql.member.dto.request.notification.TokenRegisterRequest;
import synapps.resona.api.mysql.member.dto.response.notification.NotificationResponse;
import synapps.resona.api.mysql.member.dto.response.notification.NotificationSettingResponse;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.notification.MemberNotification;
import synapps.resona.api.mysql.member.entity.notification.MemberNotificationSetting;
import synapps.resona.api.mysql.member.entity.notification.MemberPushToken;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.exception.NotificationException;
import synapps.resona.api.mysql.member.repository.MemberNotificationRepository;
import synapps.resona.api.mysql.member.repository.MemberNotificationSettingRepository;
import synapps.resona.api.mysql.member.repository.MemberPushTokenRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberNotificationService {

  private final Logger logger = LogManager.getLogger(MemberNotificationService.class);

  private final MemberNotificationRepository notificationRepository;
  private final MemberNotificationSettingRepository notificationSettingRepository;
  private final MemberPushTokenRepository pushTokenRepository;
  private final MemberRepository memberRepository;
  private final MemberService memberService;


  @Transactional
  public String registerToken(TokenRegisterRequest tokenRegisterRequest) {
    String memberEmail = memberService.getMemberEmail();
    Member member = memberRepository.findByEmail(memberEmail)
        .orElseThrow(MemberException::memberNotFound);

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
  public NotificationSettingResponse getNotificationSetting() {
    String memberEmail = memberService.getMemberEmail();
    Member member = memberRepository.findByEmail(memberEmail)
        .orElseThrow(MemberException::memberNotFound);

    MemberNotificationSetting setting = notificationSettingRepository.findByMember(member)
        .orElseGet(() -> {
          MemberNotificationSetting newSetting = MemberNotificationSetting.of(member, true, true, true, true);
          return notificationSettingRepository.save(newSetting);
        });

    return NotificationSettingResponse.to(setting);
  }

  @Transactional
  public void updateNotificationSetting(NotificationSettingUpdateRequest request) {
    String memberEmail = memberService.getMemberEmail();
    Member member = memberRepository.findByEmail(memberEmail)
        .orElseThrow(MemberException::memberNotFound);

    MemberNotificationSetting setting = notificationSettingRepository.findByMember(member)
        .orElseThrow(NotificationException::notificationSettingNotFound);

    setting.update(request.isFriendRequestEnabled(), request.isMarketingEnabled(),
        request.isServiceNotificationEnabled(), request.isSocialNotificationEnabled());
  }

  @Transactional(readOnly = true)
  public CursorResult<NotificationResponse> getNotifications(Long cursorId, Integer size) {
    String memberEmail = memberService.getMemberEmail();
    Member member = memberRepository.findByEmail(memberEmail)
        .orElseThrow(MemberException::memberNotFound);

    List<MemberNotification> notifications = notificationRepository.findByMemberAndIdLessThanOrderByIdDesc(member, cursorId, PageRequest.of(0, size));
    List<NotificationResponse> notificationResponses = notifications.stream()
        .map(NotificationResponse::to)
        .toList();

    Long lastId = notifications.isEmpty() ? -1L : notifications.get(notifications.size() - 1).getId();
    return new CursorResult<>(notificationResponses, lastId != -1L, String.valueOf(lastId));
  }

  @Transactional
  public void readNotification(Long notificationId) {
    String memberEmail = memberService.getMemberEmail();
    Member member = memberRepository.findByEmail(memberEmail)
        .orElseThrow(MemberException::memberNotFound);

    MemberNotification notification = notificationRepository.findById(notificationId)
        .orElseThrow(NotificationException::notificationNotFound);

    notification.read();
  }

  @Transactional
  public void deleteNotification(Long notificationId) {
    MemberNotification notification = notificationRepository.findById(notificationId)
        .orElseThrow(NotificationException::notificationNotFound);

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

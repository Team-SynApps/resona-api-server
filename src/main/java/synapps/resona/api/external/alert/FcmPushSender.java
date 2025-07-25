package synapps.resona.api.external.alert;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class FcmPushSender {
  private final Logger logger = LogManager.getLogger(FcmPushSender.class);

  // TODO: 예시 코드, 실제 비스지스 로직에 맞게 수정해야 함.
  public String sendPushNotification(FcmSendRequest fcmSendRequest) {
    Message message = Message.builder()
        .setToken(fcmSendRequest.token())
        .setNotification(Notification.builder()
            .setTitle("알림 제목")
            .setBody(fcmSendRequest.notificationType())
            .build())
//        .setAndroidConfig( // Android
//            AndroidConfig.builder()
//                .setNotification(
//                    AndroidNotification.builder()
//                        .setTitle("알림 제목")
//                        .setBody(fcmSendRequest.notificationType())
//                        .setClickAction("push_click")
//                        .build()
//                ).build()
//        ).setApnsConfig( // IOS
//            ApnsConfig.builder()
//                .setAps(Aps.builder()
//                    .setCategory("push_click")
////                    .setBadge(1)
//                    .setSound("default")
//                    .build()
//                ).build()
//        )
        .build();
    try {
      return FirebaseMessaging.getInstance().send(message);
    } catch (FirebaseMessagingException exception) {
      logger.error("Fcm 메시지 전송 실패 : {}", exception.getMessage());
      throw new RuntimeException(exception);
    }
  }
}
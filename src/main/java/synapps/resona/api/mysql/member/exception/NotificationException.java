package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.mysql.member.code.MemberErrorCode;

public class NotificationException extends BaseException {

  public NotificationException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  public static NotificationException of(MemberErrorCode errorCode) {
    return new NotificationException(errorCode.getMessage(), errorCode.getStatus(),
        errorCode.getCustomCode());
  }

  public static NotificationException notificationNotFound() {
    return NotificationException.of(MemberErrorCode.NOTIFICATION_NOT_FOUND);
  }

  public static NotificationException notificationSettingNotFound() {
    return NotificationException.of(MemberErrorCode.NOTIFICATION_NOT_FOUND);
  }
}

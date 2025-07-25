package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class NotificationException extends BaseException {

  public NotificationException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  public static NotificationException of(ErrorCode errorCode) {
    return new NotificationException(errorCode.getMessage(), errorCode.getStatus(),
        errorCode.getCode());
  }

  public static NotificationException notificationNotFound() {
    return NotificationException.of(ErrorCode.NOTIFICATION_NOT_FOUND);
  }

  public static NotificationException notificationSettingNotFound() {
    return NotificationException.of(ErrorCode.NOTIFICATION_NOT_FOUND);
  }
}

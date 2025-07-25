package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class NotificationException extends BaseException {

  public NotificationException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  public static NotificationException of(GlobalErrorCode globalErrorCode) {
    return new NotificationException(globalErrorCode.getMessage(), globalErrorCode.getStatus(),
        globalErrorCode.getCode());
  }

  public static NotificationException notificationNotFound() {
    return NotificationException.of(GlobalErrorCode.NOTIFICATION_NOT_FOUND);
  }

  public static NotificationException notificationSettingNotFound() {
    return NotificationException.of(GlobalErrorCode.NOTIFICATION_NOT_FOUND);
  }
}

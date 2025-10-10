package com.synapps.resona.exception;

import com.synapps.resona.code.NotificationErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NotificationException extends BaseException {

  public NotificationException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  public static NotificationException of(NotificationErrorCode errorCode) {
    return new NotificationException(errorCode.getMessage(), errorCode.getStatus(),
        errorCode.getCustomCode());
  }

  public static NotificationException notificationNotFound() {
    return NotificationException.of(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
  }

  public static NotificationException notificationSettingNotFound() {
    return NotificationException.of(NotificationErrorCode.NOTIFICATION_SETTING_NOT_FOUND);
  }
}

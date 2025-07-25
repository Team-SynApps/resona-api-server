package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class ReplyException extends BaseException {

  protected ReplyException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static ReplyException of(GlobalErrorCode globalErrorCode) {
    return new ReplyException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCode());
  }

  public static ReplyException replyNotFound() {
    return of(GlobalErrorCode.REPLY_NOT_FOUND);
  }
}

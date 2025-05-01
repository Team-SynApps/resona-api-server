package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class ReplyException extends BaseException {

  protected ReplyException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static ReplyException of(ErrorCode errorCode) {
    return new ReplyException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
  }

  public static ReplyException replyNotFound() {
    return of(ErrorCode.REPLY_NOT_FOUND);
  }
}

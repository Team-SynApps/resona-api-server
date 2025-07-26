package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.mysql.socialMedia.code.SocialErrorCode;

public class ReplyException extends BaseException {

  protected ReplyException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static ReplyException of(SocialErrorCode errorCode) {
    return new ReplyException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static ReplyException replyNotFound() {
    return of(SocialErrorCode.REPLY_NOT_FOUND);
  }
}

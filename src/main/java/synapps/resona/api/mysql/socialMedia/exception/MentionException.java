package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class MentionException extends BaseException {

  protected MentionException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static MentionException of(ErrorCode errorCode) {
    return new MentionException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
  }

  public static MentionException mentionNotFound() {
    return of(ErrorCode.MENTION_NOT_FOUND);
  }

}

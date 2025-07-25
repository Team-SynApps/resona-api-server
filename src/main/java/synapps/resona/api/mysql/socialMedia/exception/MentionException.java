package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class MentionException extends BaseException {

  protected MentionException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static MentionException of(GlobalErrorCode globalErrorCode) {
    return new MentionException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCode());
  }

  public static MentionException mentionNotFound() {
    return of(GlobalErrorCode.MENTION_NOT_FOUND);
  }

}

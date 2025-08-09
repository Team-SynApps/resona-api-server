package synapps.resona.api.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.socialMedia.code.SocialErrorCode;

public class MentionException extends BaseException {

  protected MentionException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static MentionException of(SocialErrorCode errorCode) {
    return new MentionException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static MentionException mentionNotFound() {
    return of(SocialErrorCode.MENTION_NOT_FOUND);
  }

}

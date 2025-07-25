package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.mysql.socialMedia.code.SocialErrorCode;

public class LikeException extends BaseException {

  protected LikeException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static LikeException of(SocialErrorCode errorCode) {
    return new LikeException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static LikeException likeNotFound() {
    return of(SocialErrorCode.LIKE_NOT_FOUND);
  }
}

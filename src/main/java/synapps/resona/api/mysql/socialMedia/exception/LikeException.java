package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class LikeException extends BaseException {

  protected LikeException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static LikeException of(ErrorCode errorCode) {
    return new LikeException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
  }

  public static LikeException likeNotFound() {
    return of(ErrorCode.LIKE_NOT_FOUND);
  }
}

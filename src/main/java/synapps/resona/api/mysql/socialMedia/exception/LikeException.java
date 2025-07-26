package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class LikeException extends BaseException {

  protected LikeException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static LikeException of(GlobalErrorCode globalErrorCode) {
    return new LikeException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCustomCode());
  }

  public static LikeException likeNotFound() {
    return of(GlobalErrorCode.LIKE_NOT_FOUND);
  }
}

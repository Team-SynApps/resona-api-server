package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class FeedMediaException extends BaseException {

  protected FeedMediaException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static FeedMediaException of(GlobalErrorCode globalErrorCode) {
    return new FeedMediaException(globalErrorCode.getMessage(), globalErrorCode.getStatus(),
        globalErrorCode.getCode());
  }

  public static FeedMediaException imageNotFound() {
    return of(GlobalErrorCode.IMAGE_NOT_FOUND);
  }
}

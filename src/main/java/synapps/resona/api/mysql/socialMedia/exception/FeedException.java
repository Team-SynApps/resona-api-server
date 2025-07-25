package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class FeedException extends BaseException {

  protected FeedException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static FeedException of(GlobalErrorCode globalErrorCode) {
    return new FeedException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCode());
  }

  public static FeedException feedNotFoundException() {
    return of(GlobalErrorCode.FEED_NOT_FOUND);
  }
}

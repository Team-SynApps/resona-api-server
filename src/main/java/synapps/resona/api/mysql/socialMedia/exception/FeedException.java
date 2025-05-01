package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class FeedException extends BaseException {

  protected FeedException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static FeedException of(ErrorCode errorCode) {
    return new FeedException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
  }

  public static FeedException feedNotFoundException() {
    return of(ErrorCode.FEED_NOT_FOUND);
  }
}

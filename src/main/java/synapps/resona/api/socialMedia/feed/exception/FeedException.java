package synapps.resona.api.socialMedia.feed.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.socialMedia.code.SocialErrorCode;

public class FeedException extends BaseException {

  protected FeedException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static FeedException of(SocialErrorCode errorCode) {
    return new FeedException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static FeedException feedNotFoundException() {
    return of(SocialErrorCode.FEED_NOT_FOUND);
  }
}

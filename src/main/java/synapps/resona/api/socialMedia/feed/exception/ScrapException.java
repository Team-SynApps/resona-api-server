package synapps.resona.api.socialMedia.feed.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.socialMedia.code.SocialErrorCode;

public class ScrapException extends BaseException {

  protected ScrapException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static ScrapException of(SocialErrorCode errorCode) {
    return new ScrapException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static ScrapException scrapNotFound() {
    return of(SocialErrorCode.SCRAP_NOT_FOUND);
  }

  public static ScrapException scrapAlreadyExist() {
    return of(SocialErrorCode.SCRAP_ALREADY_EXIST);
  }
}

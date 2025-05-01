package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class ScrapException extends BaseException {

  protected ScrapException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static ScrapException of(ErrorCode errorCode) {
    return new ScrapException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
  }

  public static ScrapException scrapNotFound() {
    return of(ErrorCode.SCRAP_NOT_FOUND);
  }

  public static ScrapException scrapAlreadyExist() {
    return of(ErrorCode.SCRAP_ALREADY_EXIST);
  }
}

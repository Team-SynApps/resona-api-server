package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class ScrapException extends BaseException {

  protected ScrapException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static ScrapException of(GlobalErrorCode globalErrorCode) {
    return new ScrapException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCustomCode());
  }

  public static ScrapException scrapNotFound() {
    return of(GlobalErrorCode.SCRAP_NOT_FOUND);
  }

  public static ScrapException scrapAlreadyExist() {
    return of(GlobalErrorCode.SCRAP_ALREADY_EXIST);
  }
}

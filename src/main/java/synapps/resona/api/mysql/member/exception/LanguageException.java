package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class LanguageException extends BaseException {

  public LanguageException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static LanguageException of(GlobalErrorCode globalErrorCode) {
    return new LanguageException(globalErrorCode.getMessage(), globalErrorCode.getStatus(),
        globalErrorCode.getCode());
  }

  public static LanguageException languageNotFound() {
    return LanguageException.of(GlobalErrorCode.LANGUAGE_NOT_FOUND);
  }
}

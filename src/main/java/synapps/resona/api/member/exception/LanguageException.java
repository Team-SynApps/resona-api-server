package synapps.resona.api.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.member.code.MemberErrorCode;

public class LanguageException extends BaseException {

  public LanguageException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static LanguageException of(MemberErrorCode errorCode) {
    return new LanguageException(errorCode.getMessage(), errorCode.getStatus(),
        errorCode.getCustomCode());
  }

  public static LanguageException languageNotFound() {
    return LanguageException.of(MemberErrorCode.LANGUAGE_NOT_FOUND);
  }
}

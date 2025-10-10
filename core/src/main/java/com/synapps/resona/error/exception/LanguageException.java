package com.synapps.resona.error.exception;

import com.synapps.resona.error.GlobalErrorCode;
import org.springframework.http.HttpStatus;

public class LanguageException extends BaseException {

  public LanguageException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static LanguageException of(GlobalErrorCode errorCode) {
    return new LanguageException(errorCode.getMessage(), errorCode.getStatus(),
        errorCode.getCustomCode());
  }

  public static LanguageException languageNotFound() {
    return LanguageException.of(GlobalErrorCode.LANGUAGE_NOT_FOUND);
  }
}

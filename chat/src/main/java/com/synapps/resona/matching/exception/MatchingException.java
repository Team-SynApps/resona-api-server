package com.synapps.resona.matching.exception;

import com.synapps.resona.error.exception.BaseException;
import com.synapps.resona.matching.code.MatchingErrorCode;
import org.springframework.http.HttpStatus;

public class MatchingException extends BaseException {

  protected MatchingException(String message, HttpStatus status,
      String errorCode) {
    super(message, status, errorCode);
  }

  private static MatchingException of(MatchingErrorCode errorCode) {
    return new MatchingException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static MatchingException matchFailed() {
    return of(MatchingErrorCode.MATCHING_FAILED);
  }
}

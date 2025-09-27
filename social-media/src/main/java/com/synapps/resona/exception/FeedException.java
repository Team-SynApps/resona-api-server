package com.synapps.resona.exception;

import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

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

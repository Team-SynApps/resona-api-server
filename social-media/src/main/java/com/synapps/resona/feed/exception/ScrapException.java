package com.synapps.resona.feed.exception;

import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

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

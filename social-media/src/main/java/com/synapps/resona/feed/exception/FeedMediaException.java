package com.synapps.resona.feed.exception;

import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class FeedMediaException extends BaseException {

  protected FeedMediaException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static FeedMediaException of(SocialErrorCode errorCode) {
    return new FeedMediaException(errorCode.getMessage(), errorCode.getStatus(),
        errorCode.getCustomCode());
  }

  public static FeedMediaException imageNotFound() {
    return of(SocialErrorCode.IMAGE_NOT_FOUND);
  }
}

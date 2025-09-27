package com.synapps.resona.exception;

import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CommentException extends BaseException {

  protected CommentException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static CommentException of(SocialErrorCode errorCode) {
    return new CommentException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static CommentException commentNotFound() {
    return of(SocialErrorCode.COMMENT_NOT_FOUND);
  }
}

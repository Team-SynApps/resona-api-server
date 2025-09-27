package com.synapps.resona.comment.exception;

import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReplyException extends BaseException {

  protected ReplyException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static ReplyException of(SocialErrorCode errorCode) {
    return new ReplyException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static ReplyException replyNotFound() {
    return of(SocialErrorCode.REPLY_NOT_FOUND);
  }
}

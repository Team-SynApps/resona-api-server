package com.synapps.resona.comment.exception;

import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MentionException extends BaseException {

  protected MentionException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static MentionException of(SocialErrorCode errorCode) {
    return new MentionException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static MentionException mentionNotFound() {
    return of(SocialErrorCode.MENTION_NOT_FOUND);
  }

}

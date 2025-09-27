package com.synapps.resona.likes.exception;

import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class LikeException extends BaseException {

  protected LikeException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static LikeException of(SocialErrorCode errorCode) {
    return new LikeException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static LikeException likeNotFound() {
    return of(SocialErrorCode.LIKE_NOT_FOUND);
  }

  public static LikeException alreadyLiked() {
    return of(SocialErrorCode.ALREADY_LIKED);
  }
}

package com.synapps.resona.exception;

import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class HobbyException extends BaseException {

  public HobbyException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static HobbyException of(MemberErrorCode errorCode) {
    return new HobbyException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static HobbyException hobbyNotFound() {
    return of(MemberErrorCode.HOBBY_NOT_FOUND);
  }
}

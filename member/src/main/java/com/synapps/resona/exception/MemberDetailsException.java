package com.synapps.resona.exception;

import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MemberDetailsException extends BaseException {

  protected MemberDetailsException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static MemberDetailsException of(MemberErrorCode errorCode) {
    return new MemberDetailsException(errorCode.getMessage(), errorCode.getStatus(),
        errorCode.getCustomCode());
  }

  public static MemberDetailsException memberDetailsNotFound() {
    return of(MemberErrorCode.MEMBER_DETAILS_NOT_FOUND);
  }
}

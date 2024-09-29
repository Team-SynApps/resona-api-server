package com.synapps.atch.mysql.member.exception;

import com.synapps.atch.global.exception.BaseException;
import com.synapps.atch.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class MemberException extends BaseException {
  public MemberException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static MemberException of(ErrorCode errorCode) {
    return new MemberException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
  }

  public static MemberException memberNotFound() {
    return of(ErrorCode.MEMBER_NOT_FOUND);
  }

  public static MemberException duplicateEmail() {
    return of(ErrorCode.DUPLICATE_EMAIL);
  }

  public static MemberException invalidPassword() {
    return of(ErrorCode.INVALID_PASSWORD);
  }
  public static MemberException invalidTimeStamp(){
    return of(ErrorCode.INVALID_TIMESTAMP);
  }
}
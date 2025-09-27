package com.synapps.resona.exception;

import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MemberException extends BaseException {

  public MemberException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static MemberException of(MemberErrorCode errorCode) {
    return new MemberException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static MemberException memberNotFound() {
    return of(MemberErrorCode.MEMBER_NOT_FOUND);
  }

  public static MemberException duplicateEmail() {
    return of(MemberErrorCode.DUPLICATE_EMAIL);
  }

  public static MemberException invalidPassword() {
    return of(MemberErrorCode.INVALID_PASSWORD);
  }

  public static MemberException invalidTimeStamp() {
    return of(MemberErrorCode.INVALID_TIMESTAMP);
  }

  public static MemberException unAuthenticatedRequest() {
    return of(MemberErrorCode.UNAUTHENTICATED_REQUEST);
  }

  public static MemberException followingNotFound() {
    return of(MemberErrorCode.FOLLOWING_NOT_FOUND);
  }

  public static MemberException memberPasswordBlank() {
    return of(MemberErrorCode.MEMBER_PASSWORD_BLANK);
  }

  public static MemberException invalidPasswordPolicy() {
    return of(MemberErrorCode.INVALID_PASSWORD_POLICY);
  }
}
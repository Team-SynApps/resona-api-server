package com.synapps.resona.exception;

import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProfileException extends BaseException {

  public ProfileException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static ProfileException of(MemberErrorCode errorCode) {
    return new ProfileException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static ProfileException invalidProfile() {
    return of(MemberErrorCode.PROFILE_INPUT_INVALID);
  }

  public static ProfileException profileNotFound() {
    return of(MemberErrorCode.PROFILE_NOT_FOUND);
  }

  public static ProfileException duplicateTag() {
    return of(MemberErrorCode.DUPLICATE_TAG);
  }
}

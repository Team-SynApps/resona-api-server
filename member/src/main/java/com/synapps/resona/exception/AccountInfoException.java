package com.synapps.resona.exception;

import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AccountInfoException extends BaseException {

  protected AccountInfoException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static AccountInfoException of(MemberErrorCode errorCode) {
    return new AccountInfoException(errorCode.getMessage(), errorCode.getStatus(),
        errorCode.getCustomCode());
  }

  private static AccountInfoException accountNotFound() {
    return of(MemberErrorCode.ACCOUNT_INFO_NOT_FOUND);
  }

  public static AccountInfoException accountInfoNotFound() {
    return of(MemberErrorCode.ACCOUNT_BANNED);
  }
}

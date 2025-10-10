package com.synapps.resona.exception;

import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BlockException extends BaseException {

  protected BlockException(String message, HttpStatus status,
      String errorCode) {
    super(message, status, errorCode);
  }

  private static BlockException of(MemberErrorCode code) {
    return new BlockException(code.getMessage(), code.getStatus(), code.getCustomCode());
  }

  public static BlockException cannotBlockSelf() {
    return of(MemberErrorCode.CANNOT_BLOCK_SELF);
  }

  public static BlockException alreadyBlocked() {
    return of(MemberErrorCode.ALREADY_BLOCKED);
  }

  public static BlockException notBlocked() {
    return of(MemberErrorCode.NOT_BLOCKED);
  }
}

package com.synapps.resona.error;

import com.synapps.resona.dto.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements ErrorCode {
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER001", "Internal Server Error"),
  INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "Invalid Input"),
  LANGUAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "LANG001", "Invalid Language code"),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;

  GlobalErrorCode(final HttpStatus status, final String code, final String message) {
    this.code = code;
    this.status = status;
    this.message = message;
  }

  @Override
  public HttpStatus getStatus() {
    return this.status;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  @Override
  public int getStatusCode() {
    return status.value();
  }

  @Override
  public String getCustomCode() {
    return this.code;
  }
}

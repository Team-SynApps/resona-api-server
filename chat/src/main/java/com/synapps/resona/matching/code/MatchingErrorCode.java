package com.synapps.resona.matching.code;

import com.synapps.resona.dto.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MatchingErrorCode implements ErrorCode {
  MATCHING_FAILED(HttpStatus.NOT_FOUND, "MATCH001", "Cannot find chat partner.");

  private final String code;
  private final String message;
  private final HttpStatus status;

  MatchingErrorCode(final HttpStatus status, final String code, final String message) {
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

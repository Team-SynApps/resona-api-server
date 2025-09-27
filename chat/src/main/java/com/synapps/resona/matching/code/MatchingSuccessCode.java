package com.synapps.resona.matching.code;

import com.synapps.resona.dto.code.SuccessCode;
import org.springframework.http.HttpStatus;

public enum MatchingSuccessCode implements SuccessCode {
  MATCHING_SUCCESS(HttpStatus.CREATED, "Matching successful and chat room created.");

  private final HttpStatus status;
  private final String message;

  MatchingSuccessCode(HttpStatus status, String message) {
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
    return this.status.value();
  }
}
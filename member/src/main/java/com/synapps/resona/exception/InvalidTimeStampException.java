package com.synapps.resona.exception;

import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidTimeStampException extends BaseException {

  public InvalidTimeStampException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  public static InvalidTimeStampException of(String message, HttpStatus status, String errorCode) {
    return new InvalidTimeStampException(message, status, errorCode);
  }
}

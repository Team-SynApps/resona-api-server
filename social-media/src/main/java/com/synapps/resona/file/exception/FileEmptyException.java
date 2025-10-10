package com.synapps.resona.file.exception;

import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class FileEmptyException extends BaseException {

  protected FileEmptyException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  public static FileEmptyException of(String message, HttpStatus status, String errorCode) {
    return new FileEmptyException(message, status, errorCode);
  }
}

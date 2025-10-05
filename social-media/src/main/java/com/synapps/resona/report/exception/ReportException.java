package com.synapps.resona.report.exception;

import com.synapps.resona.code.SocialErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReportException extends BaseException {

  protected ReportException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static ReportException of(SocialErrorCode errorCode) {
    return new ReportException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static ReportException reportNotFound() {
    return of(SocialErrorCode.REPORT_NOT_FOUND);
  }

  public static ReportException alreadyReported() {
    return of(SocialErrorCode.ALREADY_REPORTED);
  }

  public static ReportException alreadyExecuted() {
    return of(SocialErrorCode.ALREADY_EXECUTED);
  }
}

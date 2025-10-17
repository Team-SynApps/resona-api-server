package com.synapps.resona.report.exception;

import com.synapps.resona.error.exception.BaseException;
import com.synapps.resona.report.code.ReportErrorCode;
import org.springframework.http.HttpStatus;

public class ReportException extends BaseException {

    public ReportException(String message, HttpStatus status, String errorCode) {
        super(message, status, errorCode);
    }

    private static ReportException of(ReportErrorCode errorCode) {
        return new ReportException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
    }

    public static ReportException reportFailed() {
        return of(ReportErrorCode.REPORT_FAILED);
    }

    public static ReportException alreadyReported() {
        return of(ReportErrorCode.ALREADY_REPORTED);
    }
    
    public static ReportException alreadyExecuted() {
        return of(ReportErrorCode.ALREADY_EXECUTED);
    }

    public static ReportException reportNotFound() {
        return of(ReportErrorCode.REPORT_NOT_FOUND);
    }
}
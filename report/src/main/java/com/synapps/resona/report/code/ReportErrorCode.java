package com.synapps.resona.report.code;

import com.synapps.resona.dto.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ReportErrorCode implements ErrorCode {

    REPORT_FAILED(HttpStatus.BAD_REQUEST, "R001", "Report failed."),
    ALREADY_REPORTED(HttpStatus.BAD_REQUEST, "R002", "You have already reported this."),
    ALREADY_EXECUTED(HttpStatus.BAD_REQUEST, "R003", "This report has already been executed."),
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "R004", "Report not found.");

    private final HttpStatus status;
    private final String customCode;
    private final String message;

    ReportErrorCode(HttpStatus status, String customCode, String message) {
        this.status = status;
        this.customCode = customCode;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCustomCode() {
        return customCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getStatusCode() {
        return status.value();
    }
}

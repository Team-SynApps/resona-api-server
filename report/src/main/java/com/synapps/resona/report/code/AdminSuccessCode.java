package com.synapps.resona.report.code;

import com.synapps.resona.dto.code.SuccessCode;
import org.springframework.http.HttpStatus;

public enum AdminSuccessCode implements SuccessCode {

    SANCTION_SUCCESS(HttpStatus.OK, "User sanctioned successfully.");

    private final HttpStatus status;
    private final String message;

    AdminSuccessCode(HttpStatus status, String message) {
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

package com.synapps.resona.member.error;

import com.synapps.resona.dto.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER001", "Member not found"),
    DUPLICATE_MEMBER(HttpStatus.CONFLICT, "MEMBER002", "Duplicate member");

    private final String code;
    private final String message;
    private final HttpStatus status;

    MemberErrorCode(final HttpStatus status, final String code, final String message) {
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

package com.synapps.resona.external.email.exception;

import com.synapps.resona.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailException extends MessagingException {
    private final HttpStatus status;
    private final String errorCode;

    public EmailException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    private static EmailException of(ErrorCode errorCode) {
        return new EmailException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
    }

    public static EmailException invalidEmailCode() {
        return of(ErrorCode.INVALID_EMAIL_CODE);
    }

    public static EmailException emailSendFailed() {
        return of(ErrorCode.EMAIL_SEND_FAILED);
    }
}

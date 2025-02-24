package synapps.resona.api.external.email.exception;

import jakarta.mail.MessagingException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.ErrorCode;

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

    public static EmailException blankCode() {
        return of(ErrorCode.BLANK_CODE);
    }

    public static EmailException trialExceeded() {
        return of(ErrorCode.TRIAL_EXCEEDED);
    }
}

package synapps.resona.api.external.email.exception;

import jakarta.mail.MessagingException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.ErrorCode;

import java.util.HashMap;

@Getter
public class EmailException extends MessagingException {
    private final HttpStatus status;
    private final String errorCode;

    private Integer mailCheckCountLeft;

    public EmailException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    private EmailException(HttpStatus status, String errorCode) {
        this.status = status;
        this.errorCode = errorCode;
    }

    private void addMailCheckCountLeft(Integer mailCheckCountLeft) {
        this.mailCheckCountLeft = mailCheckCountLeft;
    }

    private static EmailException of(ErrorCode errorCode) {
        return new EmailException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
    }

    private static EmailException mailCheck(Integer mailCheckCountLeft) {
        EmailException emailException = new EmailException(ErrorCode.INVALID_EMAIL_CODE.getMessage(), ErrorCode.INVALID_EMAIL_CODE.getStatus(), ErrorCode.INVALID_EMAIL_CODE.getCode());
        emailException.addMailCheckCountLeft(mailCheckCountLeft);
        return emailException;
    }

    public static EmailException invalidEmailCode(Integer mailCheckCountLeft) {
        return mailCheck(mailCheckCountLeft);
    }

    public static EmailException emailSendFailed() {
        return of(ErrorCode.EMAIL_SEND_FAILED);
    }

    public static EmailException sendTrialExceeded() {
        return of(ErrorCode.SEND_TRIAL_EXCEEDED);
    }

    public static EmailException verifyTrialExceeded() {
        return of(ErrorCode.VERIFY_TRIAL_EXCEEDED);
    }

    public static EmailException emailCodeExpired() {
        return of(ErrorCode.CODE_EXPIRED);
    }

    public static EmailException emailCodeNotFound() {
        return of(ErrorCode.CODE_NOT_FOUND);
    }
}

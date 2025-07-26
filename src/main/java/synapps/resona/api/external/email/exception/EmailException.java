package synapps.resona.api.external.email.exception;

import jakarta.mail.MessagingException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import synapps.resona.api.external.email.code.EmailErrorCode;
import synapps.resona.api.global.error.GlobalErrorCode;

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

  private static EmailException of(EmailErrorCode errorCode) {
    return new EmailException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  private static EmailException mailCheck(Integer mailCheckCountLeft) {
    EmailException emailException = new EmailException(EmailErrorCode.INVALID_EMAIL_CODE.getMessage(),
        EmailErrorCode.INVALID_EMAIL_CODE.getStatus(), EmailErrorCode.INVALID_EMAIL_CODE.getCustomCode());
    emailException.addMailCheckCountLeft(mailCheckCountLeft);
    return emailException;
  }

  public static EmailException invalidEmailCode(Integer mailCheckCountLeft) {
    return mailCheck(mailCheckCountLeft);
  }

  public static EmailException emailSendFailed() {
    return of(EmailErrorCode.EMAIL_SEND_FAILED);
  }

  public static EmailException sendTrialExceeded() {
    return of(EmailErrorCode.SEND_TRIAL_EXCEEDED);
  }

  public static EmailException verifyTrialExceeded() {
    return of(EmailErrorCode.VERIFY_TRIAL_EXCEEDED);
  }

  public static EmailException emailCodeExpired() {
    return of(EmailErrorCode.CODE_EXPIRED);
  }

  public static EmailException emailCodeNotFound() {
    return of(EmailErrorCode.CODE_NOT_FOUND);
  }

  private void addMailCheckCountLeft(Integer mailCheckCountLeft) {
    this.mailCheckCountLeft = mailCheckCountLeft;
  }
}

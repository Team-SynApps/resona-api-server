package synapps.resona.api.external.email.exception;

import jakarta.mail.MessagingException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.GlobalErrorCode;

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

  private static EmailException of(GlobalErrorCode globalErrorCode) {
    return new EmailException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCustomCode());
  }

  private static EmailException mailCheck(Integer mailCheckCountLeft) {
    EmailException emailException = new EmailException(GlobalErrorCode.INVALID_EMAIL_CODE.getMessage(),
        GlobalErrorCode.INVALID_EMAIL_CODE.getStatus(), GlobalErrorCode.INVALID_EMAIL_CODE.getCustomCode());
    emailException.addMailCheckCountLeft(mailCheckCountLeft);
    return emailException;
  }

  public static EmailException invalidEmailCode(Integer mailCheckCountLeft) {
    return mailCheck(mailCheckCountLeft);
  }

  public static EmailException emailSendFailed() {
    return of(GlobalErrorCode.EMAIL_SEND_FAILED);
  }

  public static EmailException sendTrialExceeded() {
    return of(GlobalErrorCode.SEND_TRIAL_EXCEEDED);
  }

  public static EmailException verifyTrialExceeded() {
    return of(GlobalErrorCode.VERIFY_TRIAL_EXCEEDED);
  }

  public static EmailException emailCodeExpired() {
    return of(GlobalErrorCode.CODE_EXPIRED);
  }

  public static EmailException emailCodeNotFound() {
    return of(GlobalErrorCode.CODE_NOT_FOUND);
  }

  private void addMailCheckCountLeft(Integer mailCheckCountLeft) {
    this.mailCheckCountLeft = mailCheckCountLeft;
  }
}

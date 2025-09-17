package synapps.resona.api.external.email.code;

import java.util.Arrays;
import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.ErrorCode;

public enum EmailErrorCode implements ErrorCode {
  INVALID_EMAIL_CODE(HttpStatus.UNAUTHORIZED, "EMAIL001", "Invalid email code"),
  EMAIL_SEND_FAILED(HttpStatus.CONFLICT, "EMAIL002", "Email send failed"),
  CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "EMAIL003", "Email code not found"),
  SEND_TRIAL_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "EMAIL004", "Email send trial exceeded"),
  NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "EMAIL005", "Email code does not match"),
  CODE_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL006", "Email code expired"),
  VERIFY_TRIAL_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "EMAIL007", "Email verify trial exceeded"),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;

  EmailErrorCode(final HttpStatus status, final String code, final String message) {
    this.code = code;
    this.status = status;
    this.message = message;
  }

  public static EmailErrorCode fromErrorCode(String customCode) {
    return Arrays.stream(EmailErrorCode.values())
        .filter(e -> e.getCustomCode().equals(customCode))
        .findFirst()
        .orElse(EmailErrorCode.EMAIL_SEND_FAILED);
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

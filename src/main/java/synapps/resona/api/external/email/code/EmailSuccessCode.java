package synapps.resona.api.external.email.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.SuccessCode;

public enum EmailSuccessCode implements SuccessCode {

  SEND_VERIFICATION_EMAIL_SUCCESS(HttpStatus.OK, "Verification email sent successfully."),
  EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK, "Email verification successful.");

  private final HttpStatus status;
  private final String message;

  EmailSuccessCode(HttpStatus status, String message) {
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
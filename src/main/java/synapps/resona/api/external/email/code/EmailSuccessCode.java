package synapps.resona.api.external.email.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.success.SuccessCode;

public enum EmailSuccessCode implements SuccessCode {

  SEND_VERIFICATION_EMAIL_SUCCESS(HttpStatus.OK, "인증 이메일 발송에 성공하였습니다."),
  EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK, "이메일 인증에 성공하였습니다.");

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
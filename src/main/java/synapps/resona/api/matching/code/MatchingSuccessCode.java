package synapps.resona.api.matching.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.SuccessCode;

public enum MatchingSuccessCode implements SuccessCode {
  MATCHING_SUCCESS(HttpStatus.CREATED, "매칭에 성공하고 채팅방을 생성했습니다.");

  private final HttpStatus status;
  private final String message;

  MatchingSuccessCode(HttpStatus status, String message) {
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
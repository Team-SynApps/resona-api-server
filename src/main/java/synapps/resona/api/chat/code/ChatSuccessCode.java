package synapps.resona.api.chat.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.SuccessCode;

public enum ChatSuccessCode implements SuccessCode {
  MESSAGE_SENT_SUCCESS(HttpStatus.CREATED, "메시지 전송에 성공하였습니다."),
  GET_MESSAGES_SUCCESS(HttpStatus.OK, "메시지 목록 조회에 성공하였습니다."),
  ROOM_CREATED_SUCCESS(HttpStatus.CREATED, "채팅방 생성에 성공하였습니다.");

  private final HttpStatus status;
  private final String message;

  ChatSuccessCode(HttpStatus status, String message) {
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

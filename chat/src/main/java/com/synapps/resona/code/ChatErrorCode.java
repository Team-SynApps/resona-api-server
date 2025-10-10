package com.synapps.resona.code;

import com.synapps.resona.dto.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ChatErrorCode implements ErrorCode {
  ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT001", "Chat room not found"),
  NOT_A_MEMBER(HttpStatus.FORBIDDEN, "CHAT002", "Not a member of this chat room"),
  SENDER_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT003", "Sender information not found"),
  CANNOT_CREATE_ROOM_WITH_SELF(HttpStatus.BAD_REQUEST, "CHAT004", "Cannot create a room with only yourself");

  private final String code;
  private final String message;
  private final HttpStatus status;

  ChatErrorCode(final HttpStatus status, final String code, final String message) {
    this.code = code;
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
    return status.value();
  }

  @Override
  public String getCustomCode() {
    return this.code;
  }
}

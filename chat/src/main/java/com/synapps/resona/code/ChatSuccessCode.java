package com.synapps.resona.code;

import com.synapps.resona.dto.code.SuccessCode;
import org.springframework.http.HttpStatus;

public enum ChatSuccessCode implements SuccessCode {
  MESSAGE_SENT_SUCCESS(HttpStatus.CREATED, "Message sent successfully."),
  GET_MESSAGES_SUCCESS(HttpStatus.OK, "Successfully retrieved message list."),
  ROOM_CREATED_SUCCESS(HttpStatus.CREATED, "Chat room created successfully.");

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

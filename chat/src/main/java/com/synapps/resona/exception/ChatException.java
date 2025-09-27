package com.synapps.resona.exception;

import com.synapps.resona.code.ChatErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;


public class ChatException extends BaseException {

  protected ChatException(String message, HttpStatus status,
      String errorCode) {
    super(message, status, errorCode);
  }

  private static ChatException of(ChatErrorCode errorCode) {
    return new ChatException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static ChatException notMemberInRoom() {
    return ChatException.of(ChatErrorCode.NOT_A_MEMBER);
  }

  public static ChatException senderNotFound() {
    return ChatException.of(ChatErrorCode.SENDER_NOT_FOUND);
  }

  public static ChatException cannotCreateRoomWithSelf() {
    return ChatException.of(ChatErrorCode.CANNOT_CREATE_ROOM_WITH_SELF);
  }
}

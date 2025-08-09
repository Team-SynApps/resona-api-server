package synapps.resona.api.chat.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.chat.code.ChatErrorCode;
import synapps.resona.api.global.error.exception.BaseException;


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
}

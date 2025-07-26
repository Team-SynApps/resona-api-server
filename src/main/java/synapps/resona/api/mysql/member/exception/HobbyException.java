package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.mysql.member.code.MemberErrorCode;

public class HobbyException extends BaseException {

  public HobbyException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static HobbyException of(MemberErrorCode errorCode) {
    return new HobbyException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static HobbyException hobbyNotFound() {
    return of(MemberErrorCode.HOBBY_NOT_FOUND);
  }
}

package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class HobbyException extends BaseException {

  public HobbyException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static HobbyException of(GlobalErrorCode globalErrorCode) {
    return new HobbyException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCode());
  }

  public static HobbyException hobbyNotFound() {
    return of(GlobalErrorCode.HOBBY_NOT_FOUND);
  }
}

package synapps.resona.api.global.error.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
  private final int status;
  private final String message;
  private final String customErrorCode;

  public static ErrorResponse of(ErrorCode errorCode) {
    return new ErrorResponse(errorCode.getStatus().value(), errorCode.getMessage(), errorCode.getCustomCode());
  }

  public static ErrorResponse of(ErrorCode errorCode, String message) {
    return new ErrorResponse(errorCode.getStatus().value(), message, errorCode.getCustomCode());
  }
}

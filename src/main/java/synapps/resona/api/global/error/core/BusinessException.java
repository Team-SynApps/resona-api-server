package synapps.resona.api.global.error.core;

import org.springframework.http.HttpStatus;

public class BusinessException extends BaseException {

  protected BusinessException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  public static BusinessException of(String message, HttpStatus status, String errorCode) {
    return new BusinessException(message, status, errorCode);
  }
}

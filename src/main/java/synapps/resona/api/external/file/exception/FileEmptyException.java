package synapps.resona.api.external.file.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.BusinessException;

public class FileEmptyException extends BaseException {
  protected FileEmptyException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  public static FileEmptyException of(String message, HttpStatus status, String errorCode) {
    return new FileEmptyException(message, status, errorCode);
  }
}

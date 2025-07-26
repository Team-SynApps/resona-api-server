package synapps.resona.api.external.file.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.ErrorCode;

public enum FileErrorCode implements ErrorCode {

  FILE_EMPTY_EXCEPTION(HttpStatus.CONFLICT, "FILE001", "File is empty"),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;

  FileErrorCode(final HttpStatus status, final String code, final String message) {
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

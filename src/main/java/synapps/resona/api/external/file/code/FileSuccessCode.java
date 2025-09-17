package synapps.resona.api.external.file.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.SuccessCode;

public enum FileSuccessCode implements SuccessCode {

  UPLOAD_FILE_SUCCESS(HttpStatus.OK, "File uploaded successfully."),
  UPLOAD_MULTIPLE_FILES_SUCCESS(HttpStatus.OK, "Multiple files uploaded successfully."),
  FINALIZE_FILE_SUCCESS(HttpStatus.OK, "File moved and finalized successfully.");

  private final HttpStatus status;
  private final String message;

  FileSuccessCode(HttpStatus status, String message) {
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
package synapps.resona.api.external.file.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.success.SuccessCode;

public enum FileSuccessCode implements SuccessCode {

  UPLOAD_FILE_SUCCESS(HttpStatus.OK, "파일 업로드에 성공하였습니다."),
  UPLOAD_MULTIPLE_FILES_SUCCESS(HttpStatus.OK, "다중 파일 업로드에 성공하였습니다."),
  FINALIZE_FILE_SUCCESS(HttpStatus.OK, "파일 이동 및 최종 처리에 성공하였습니다.");

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
package synapps.resona.api.mysql.socialMedia.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class CommentException extends BaseException {

  protected CommentException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static CommentException of(GlobalErrorCode globalErrorCode) {
    return new CommentException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCustomCode());
  }

  public static CommentException commentNotFound() {
    return of(GlobalErrorCode.COMMENT_NOT_FOUND);
  }
}

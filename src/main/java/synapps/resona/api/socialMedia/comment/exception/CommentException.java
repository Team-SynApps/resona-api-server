package synapps.resona.api.socialMedia.comment.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.socialMedia.code.SocialErrorCode;

public class CommentException extends BaseException {

  protected CommentException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static CommentException of(SocialErrorCode errorCode) {
    return new CommentException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static CommentException commentNotFound() {
    return of(SocialErrorCode.COMMENT_NOT_FOUND);
  }
}

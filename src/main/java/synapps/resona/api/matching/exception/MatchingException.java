package synapps.resona.api.matching.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.matching.code.MatchingErrorCode;

public class MatchingException extends BaseException {

  protected MatchingException(String message, HttpStatus status,
      String errorCode) {
    super(message, status, errorCode);
  }

  private static MatchingException of(MatchingErrorCode errorCode) {
    return new MatchingException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static MatchingException matchFailed() {
    return of(MatchingErrorCode.MATCHING_FAILED);
  }
}

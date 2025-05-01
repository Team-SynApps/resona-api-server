package synapps.resona.api.oauth.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class OAuthProviderMissMatchException extends BaseException {

  protected OAuthProviderMissMatchException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static OAuthProviderMissMatchException of(ErrorCode errorCode) {
    return new OAuthProviderMissMatchException(errorCode.getMessage(), errorCode.getStatus(),
        errorCode.getCode());
  }


}
package synapps.resona.api.oauth.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class OAuthProviderMissMatchException extends BaseException {

  protected OAuthProviderMissMatchException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static OAuthProviderMissMatchException of(GlobalErrorCode globalErrorCode) {
    return new OAuthProviderMissMatchException(globalErrorCode.getMessage(), globalErrorCode.getStatus(),
        globalErrorCode.getCode());
  }


}
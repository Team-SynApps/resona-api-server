package synapps.resona.api.oauth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import synapps.resona.api.global.error.core.GlobalErrorCode;
import synapps.resona.api.oauth.entity.ProviderType;

@Getter
public class OAuthException extends AuthenticationException {
  private final HttpStatus status;
  private final String errorCode;

  protected OAuthException(String message, HttpStatus status, String errorCode) {
    super(message);
    this.status = status;
    this.errorCode = errorCode;
  }

  private static OAuthException of(GlobalErrorCode globalErrorCode) {
    return new OAuthException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCustomCode());
  }

  private static OAuthException of(GlobalErrorCode globalErrorCode, String customMessage) {
    return new OAuthException(customMessage, globalErrorCode.getStatus(), globalErrorCode.getCustomCode());
  }

  public static OAuthException OAuthProviderMissMatch(ProviderType providerType) {
    String message =
        "Looks like you're signed up with wrong account. Please use your " + providerType
            + " account to login.";

    return of(GlobalErrorCode.PROVIDER_TYPE_MISSMATCH, message);
  }
}

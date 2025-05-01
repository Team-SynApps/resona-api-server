package synapps.resona.api.oauth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;
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

  private static OAuthException of(ErrorCode errorCode) {
    return new OAuthException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
  }

  private static OAuthException of(ErrorCode errorCode, String customMessage) {
    return new OAuthException(customMessage, errorCode.getStatus(), errorCode.getCode());
  }

  public static OAuthException OAuthProviderMissMatch(ProviderType providerType) {
    String message =
        "Looks like you're signed up with wrong account. Please use your " + providerType
            + " account to login.";

    return of(ErrorCode.PROVIDER_TYPE_MISSMATCH, message);
  }
}

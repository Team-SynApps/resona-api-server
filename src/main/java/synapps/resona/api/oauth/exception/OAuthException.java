package synapps.resona.api.oauth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.mysql.member.code.AuthErrorCode;
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

  private static OAuthException of(AuthErrorCode errorCode) {
    return new OAuthException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  private static OAuthException of(AuthErrorCode errorCode, String customMessage) {
    return new OAuthException(customMessage, errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static OAuthException OAuthProviderMissMatch(ProviderType providerType) {
    String message =
        "Looks like you're signed up with wrong account. Please use your " + providerType
            + " account to login.";

    return of(AuthErrorCode.PROVIDER_TYPE_MISSMATCH, message);
  }
}

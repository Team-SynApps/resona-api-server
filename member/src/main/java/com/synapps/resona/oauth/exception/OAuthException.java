package com.synapps.resona.oauth.exception;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.command.entity.account.ProviderType;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

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

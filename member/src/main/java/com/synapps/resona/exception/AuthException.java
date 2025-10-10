package com.synapps.resona.exception;

import com.synapps.resona.code.AuthErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AuthException extends BaseException {

  public AuthException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static AuthException of(AuthErrorCode errorCode) {
    return new AuthException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static AuthException invalidToken() {
    return of(AuthErrorCode.INVALID_TOKEN);
  }

  public static AuthException expiredToken() {
    return of(AuthErrorCode.EXPIRED_TOKEN);
  }

  public static AuthException invalidAccessToken() {
    return of(AuthErrorCode.INVALID_TOKEN);
  }

  public static AuthException invalidRefreshToken() {
    return of(AuthErrorCode.INVALID_REFRESH_TOKEN);
  }

  public static AuthException refreshTokenNotFound() {
    return of(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
  }

  public static AuthException accessTokenNotFound() {
    return of(AuthErrorCode.TOKEN_NOT_FOUND);
  }

  public static AuthException accessTokenNotExpired() {
    return of(AuthErrorCode.NOT_EXPIRED);
  }

}
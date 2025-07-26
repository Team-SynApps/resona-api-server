package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.mysql.member.code.AuthErrorCode;

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
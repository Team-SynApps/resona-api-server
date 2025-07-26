package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class AuthException extends BaseException {

  public AuthException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static AuthException of(GlobalErrorCode globalErrorCode) {
    return new AuthException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCustomCode());
  }

  public static AuthException invalidToken() {
    return of(GlobalErrorCode.INVALID_TOKEN);
  }

  public static AuthException expiredToken() {
    return of(GlobalErrorCode.EXPIRED_TOKEN);
  }

  public static AuthException invalidAccessToken() {
    return of(GlobalErrorCode.INVALID_TOKEN);
  }

  public static AuthException invalidRefreshToken() {
    return of(GlobalErrorCode.INVALID_REFRESH_TOKEN);
  }

  public static AuthException refreshTokenNotFound() {
    return of(GlobalErrorCode.REFRESH_TOKEN_NOT_FOUND);
  }

  public static AuthException accessTokenNotFound() {
    return of(GlobalErrorCode.TOKEN_NOT_FOUND);
  }

  public static AuthException accessTokenNotExpired() {
    return of(GlobalErrorCode.NOT_EXPIRED);
  }

}
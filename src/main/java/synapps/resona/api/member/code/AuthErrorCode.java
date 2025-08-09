package synapps.resona.api.member.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

  // auth
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH001", "Invalid token"),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH002", "Expired token"),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH003", "Invalid refresh token"),
  REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH004", "Refresh token not found"),
  TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH005", "Token not found"),
  INVALID_CLIENT(HttpStatus.UNAUTHORIZED, "AUTH006", "Invalid client"),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH000", "Invalid token"),
  NOT_EXPIRED(HttpStatus.NOT_ACCEPTABLE, "AUTH007", "Access token not Expired"),
  FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH008", "You do not have permission to access this resource."),
  LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH009", "The account information does not match"),

  // oauth
  PROVIDER_TYPE_MISSMATCH(HttpStatus.CONFLICT, "OAUTH001", "Account info missmatch"),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;

  AuthErrorCode(final HttpStatus status, final String code, final String message) {
    this.code = code;
    this.status = status;
    this.message = message;
  }

  @Override
  public HttpStatus getStatus() {
    return this.status;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  @Override
  public int getStatusCode() {
    return status.value();
  }

  @Override
  public String getCustomCode() {
    return this.code;
  }
}

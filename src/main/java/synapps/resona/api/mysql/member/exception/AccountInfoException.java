package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class AccountInfoException extends BaseException {

  protected AccountInfoException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static AccountInfoException of(ErrorCode errorCode) {
    return new AccountInfoException(errorCode.getMessage(), errorCode.getStatus(),
        errorCode.getCode());
  }

  private static AccountInfoException accountNotFound() {
    return of(ErrorCode.ACCOUNT_INFO_NOT_FOUND);
  }

  public static AccountInfoException accountInfoNotFound() {
    return of(ErrorCode.ACCOUNT_BANNED);
  }
}

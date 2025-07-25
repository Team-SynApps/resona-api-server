package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class AccountInfoException extends BaseException {

  protected AccountInfoException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static AccountInfoException of(GlobalErrorCode globalErrorCode) {
    return new AccountInfoException(globalErrorCode.getMessage(), globalErrorCode.getStatus(),
        globalErrorCode.getCode());
  }

  private static AccountInfoException accountNotFound() {
    return of(GlobalErrorCode.ACCOUNT_INFO_NOT_FOUND);
  }

  public static AccountInfoException accountInfoNotFound() {
    return of(GlobalErrorCode.ACCOUNT_BANNED);
  }
}

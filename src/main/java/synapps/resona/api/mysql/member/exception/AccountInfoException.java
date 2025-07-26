package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.mysql.member.code.MemberErrorCode;

public class AccountInfoException extends BaseException {

  protected AccountInfoException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static AccountInfoException of(MemberErrorCode errorCode) {
    return new AccountInfoException(errorCode.getMessage(), errorCode.getStatus(),
        errorCode.getCustomCode());
  }

  private static AccountInfoException accountNotFound() {
    return of(MemberErrorCode.ACCOUNT_INFO_NOT_FOUND);
  }

  public static AccountInfoException accountInfoNotFound() {
    return of(MemberErrorCode.ACCOUNT_BANNED);
  }
}

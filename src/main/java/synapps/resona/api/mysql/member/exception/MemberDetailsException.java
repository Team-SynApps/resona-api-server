package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class MemberDetailsException extends BaseException {

  protected MemberDetailsException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static MemberDetailsException of(GlobalErrorCode globalErrorCode) {
    return new MemberDetailsException(globalErrorCode.getMessage(), globalErrorCode.getStatus(),
        globalErrorCode.getCustomCode());
  }

  public static MemberDetailsException memberDetailsNotFound() {
    return of(GlobalErrorCode.MEMBER_DETAILS_NOT_FOUND);
  }
}

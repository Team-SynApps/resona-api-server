package synapps.resona.api.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.member.code.MemberErrorCode;

public class MemberDetailsException extends BaseException {

  protected MemberDetailsException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static MemberDetailsException of(MemberErrorCode errorCode) {
    return new MemberDetailsException(errorCode.getMessage(), errorCode.getStatus(),
        errorCode.getCustomCode());
  }

  public static MemberDetailsException memberDetailsNotFound() {
    return of(MemberErrorCode.MEMBER_DETAILS_NOT_FOUND);
  }
}

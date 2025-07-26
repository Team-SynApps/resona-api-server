package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class MemberException extends BaseException {

  public MemberException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static MemberException of(GlobalErrorCode globalErrorCode) {
    return new MemberException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCustomCode());
  }

  public static MemberException memberNotFound() {
    return of(GlobalErrorCode.MEMBER_NOT_FOUND);
  }

  public static MemberException duplicateEmail() {
    return of(GlobalErrorCode.DUPLICATE_EMAIL);
  }

  public static MemberException invalidPassword() {
    return of(GlobalErrorCode.INVALID_PASSWORD);
  }

  public static MemberException invalidTimeStamp() {
    return of(GlobalErrorCode.INVALID_TIMESTAMP);
  }

  public static MemberException unAuthenticatedRequest() {
    return of(GlobalErrorCode.UNAUTHENTICATED_REQUEST);
  }

  public static MemberException followingNotFound() {
    return of(GlobalErrorCode.FOLLOWING_NOT_FOUND);
  }
}
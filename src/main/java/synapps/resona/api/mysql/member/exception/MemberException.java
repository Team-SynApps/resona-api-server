package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.mysql.member.code.MemberErrorCode;

public class MemberException extends BaseException {

  public MemberException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static MemberException of(MemberErrorCode errorCode) {
    return new MemberException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static MemberException memberNotFound() {
    return of(MemberErrorCode.MEMBER_NOT_FOUND);
  }

  public static MemberException duplicateEmail() {
    return of(MemberErrorCode.DUPLICATE_EMAIL);
  }

  public static MemberException invalidPassword() {
    return of(MemberErrorCode.INVALID_PASSWORD);
  }

  public static MemberException invalidTimeStamp() {
    return of(MemberErrorCode.INVALID_TIMESTAMP);
  }

  public static MemberException unAuthenticatedRequest() {
    return of(MemberErrorCode.UNAUTHENTICATED_REQUEST);
  }

  public static MemberException followingNotFound() {
    return of(MemberErrorCode.FOLLOWING_NOT_FOUND);
  }
}
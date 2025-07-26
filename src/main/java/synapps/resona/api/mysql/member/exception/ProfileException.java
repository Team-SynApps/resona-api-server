package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.mysql.member.code.MemberErrorCode;

public class ProfileException extends BaseException {

  public ProfileException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static ProfileException of(MemberErrorCode errorCode) {
    return new ProfileException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static ProfileException invalidProfile() {
    return of(MemberErrorCode.PROFILE_INPUT_INVALID);
  }

  public static ProfileException profileNotFound() {
    return of(MemberErrorCode.PROFILE_NOT_FOUND);
  }

  public static ProfileException duplicateTag() {
    return of(MemberErrorCode.DUPLICATE_TAG);
  }
}

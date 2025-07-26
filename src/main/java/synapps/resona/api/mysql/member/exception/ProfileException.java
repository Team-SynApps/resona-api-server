package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class ProfileException extends BaseException {

  public ProfileException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static ProfileException of(GlobalErrorCode globalErrorCode) {
    return new ProfileException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCustomCode());
  }

  public static ProfileException invalidProfile() {
    return of(GlobalErrorCode.PROFILE_INPUT_INVALID);
  }

  public static ProfileException profileNotFound() {
    return of(GlobalErrorCode.PROFILE_NOT_FOUND);
  }

  public static ProfileException duplicateTag() {
    return of(GlobalErrorCode.DUPLICATE_TAG);
  }
}

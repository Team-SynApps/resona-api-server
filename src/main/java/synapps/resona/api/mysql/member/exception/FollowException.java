package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.core.BaseException;
import synapps.resona.api.global.error.core.GlobalErrorCode;

public class FollowException extends BaseException {

  protected FollowException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static FollowException of(GlobalErrorCode globalErrorCode) {
    return new FollowException(globalErrorCode.getMessage(), globalErrorCode.getStatus(), globalErrorCode.getCustomCode());
  }

  public static FollowException alreadyFollowing() {
    return of(GlobalErrorCode.ALREADY_FOLLOWING);
  }

  public static FollowException cantFollowMyself() {
    return of(GlobalErrorCode.FOLLOWING_MYSELF);
  }

  public static FollowException relationshipNotFound() {
    return of(GlobalErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND);
  }
}

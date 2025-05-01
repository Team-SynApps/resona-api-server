package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.exception.BaseException;
import synapps.resona.api.global.exception.ErrorCode;

public class FollowException extends BaseException {

  protected FollowException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static FollowException of(ErrorCode errorCode) {
    return new FollowException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCode());
  }

  public static FollowException alreadyFollowing() {
    return of(ErrorCode.ALREADY_FOLLOWING);
  }

  public static FollowException cantFollowMyself() {
    return of(ErrorCode.FOLLOWING_MYSELF);
  }

  public static FollowException relationshipNotFound() {
    return of(ErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND);
  }
}

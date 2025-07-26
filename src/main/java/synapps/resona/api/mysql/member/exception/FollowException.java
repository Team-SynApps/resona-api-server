package synapps.resona.api.mysql.member.exception;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.error.exception.BaseException;
import synapps.resona.api.global.error.GlobalErrorCode;
import synapps.resona.api.mysql.member.code.MemberErrorCode;

public class FollowException extends BaseException {

  protected FollowException(String message, HttpStatus status, String errorCode) {
    super(message, status, errorCode);
  }

  private static FollowException of(MemberErrorCode errorCode) {
    return new FollowException(errorCode.getMessage(), errorCode.getStatus(), errorCode.getCustomCode());
  }

  public static FollowException alreadyFollowing() {
    return of(MemberErrorCode.ALREADY_FOLLOWING);
  }

  public static FollowException cantFollowMyself() {
    return of(MemberErrorCode.FOLLOWING_MYSELF);
  }

  public static FollowException relationshipNotFound() {
    return of(MemberErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND);
  }
}

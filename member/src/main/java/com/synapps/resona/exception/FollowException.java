package com.synapps.resona.exception;

import com.synapps.resona.code.MemberErrorCode;
import com.synapps.resona.error.exception.BaseException;
import org.springframework.http.HttpStatus;

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

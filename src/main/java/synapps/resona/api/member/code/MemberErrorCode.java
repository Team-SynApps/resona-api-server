package synapps.resona.api.member.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.ErrorCode;

public enum MemberErrorCode implements ErrorCode {
  // member
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM001", "Member not found"),
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEM002", "Duplicate email"),
  INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "MEM003", "Invalid password"),
  INVALID_TIMESTAMP(HttpStatus.UNAUTHORIZED, "MEM004", "Invalid timestamp"),
  UNAUTHENTICATED_REQUEST(HttpStatus.FORBIDDEN, "MEM005", "Forbidden approach"),
  FOLLOWING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM006", "Member trying to Follow is Not found"),
  MEMBER_PASSWORD_BLANK(HttpStatus.CONFLICT, "MEM007", "Member password required"),
  INVALID_PASSWORD_POLICY(HttpStatus.NOT_ACCEPTABLE, "MEM008", "Member Password policy invalid"),

  // account info
  ACCOUNT_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "ACC001", "Account Info not found"),
  ACCOUNT_BANNED(HttpStatus.UNAUTHORIZED, "ACC002", "User Banned"),

  // profile
  PROFILE_INPUT_INVALID(HttpStatus.CONFLICT, "PROFILE001", "Invalid profile"),
  PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILE002", "Profile not fouond"),
  DUPLICATE_TAG(HttpStatus.CONFLICT, "PROFILE003", "Tag duplicate"),

  // member details
  MEMBER_DETAILS_NOT_FOUND(HttpStatus.NOT_FOUND, "MDETAILS001", "Member Details not found"),
  TIMESTAMP_INVALID(HttpStatus.CONFLICT, "TIMESTAMP001", "Invalid timestamp"),

  // follow
  ALREADY_FOLLOWING(HttpStatus.CONFLICT, "FOLLOW001", "Already Following"),
  FOLLOWING_MYSELF(HttpStatus.CONFLICT, "FOLLOW002", "Can't Follow myself"),
  FOLLOW_RELATIONSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLLOW003", "Can't find relationship"),

  // language
  LANGUAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "LANG001", "Invalid Language code"),

  // hobby
  HOBBY_NOT_FOUND(HttpStatus.NOT_FOUND, "HOB001", "Hobby Not Found"),

  // notification
  NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI001", "Notification not found"),
  NOTIFICATION_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI001", "Notification setting not found"),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;

  MemberErrorCode(final HttpStatus status, final String code, final String message) {
    this.code = code;
    this.status = status;
    this.message = message;
  }

  @Override
  public HttpStatus getStatus() {
    return this.status;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  @Override
  public int getStatusCode() {
    return status.value();
  }

  @Override
  public String getCustomCode() {
    return this.code;
  }
}

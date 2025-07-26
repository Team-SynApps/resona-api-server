package synapps.resona.api.global.error.core;


import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

public enum GlobalErrorCode implements ErrorCode {
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER001", "Internal Server Error"),
  INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "Invalid Input"),

  // member
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM001", "Member not found"),
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEM002", "Duplicate email"),
  INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "MEM003", "Invalid password"),
  INVALID_TIMESTAMP(HttpStatus.UNAUTHORIZED, "MEM004", "Invalid timestamp"),
  UNAUTHENTICATED_REQUEST(HttpStatus.FORBIDDEN, "MEM005", "Forbidden approach"),
  FOLLOWING_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM006", "Member trying to Follow is Not found"),

  // auth
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH001", "Invalid token"),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH002", "Expired token"),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH003", "Invalid refresh token"),
  REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH004", "Refresh token not found"),
  TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH005", "Token not found"),
  INVALID_CLIENT(HttpStatus.UNAUTHORIZED, "AUTH006", "Invalid client"),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH000", "Invalid token"),
  NOT_EXPIRED(HttpStatus.NOT_ACCEPTABLE, "AUTH007", "Access token not Expired"),
  FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH008", "You do not have permission to access this resource."),
  LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH009", "The account information does not match"),

  // oauth
  PROVIDER_TYPE_MISSMATCH(HttpStatus.CONFLICT, "OAUTH001", "Account info missmatch"),

  //email
  INVALID_EMAIL_CODE(HttpStatus.UNAUTHORIZED, "EMAIL001", "Invalid email code"),
  EMAIL_SEND_FAILED(HttpStatus.CONFLICT, "EMAIL002", "Email send failed"),
  CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "EMAIL003", "Email code not found"),
  SEND_TRIAL_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "EMAIL004", "Email send trial exceeded"),
  NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "EMAIL005", "Email code does not match"),
  CODE_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL006", "Email code expired"),
  VERIFY_TRIAL_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "EMAIL007", "Email verify trial exceeded"),

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

  // file
  FILE_EMPTY_EXCEPTION(HttpStatus.CONFLICT, "FILE001", "File is empty"),

  // language
  LANGUAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "LANG001", "Invalid Language code"),

  // hobby
  HOBBY_NOT_FOUND(HttpStatus.NOT_FOUND, "HOB001", "Hobby Not Found"),

  // feed
  FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "FEED001", "Feed not found"),

  // mention
  MENTION_NOT_FOUND(HttpStatus.NOT_FOUND, "MENT001", "Mention not found"),

  // like
  LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE001", "Like not found"),

  // reply
  REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "REPLY001", "Reply not found"),

  // scrap
  SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "SCRAP001", "Scrap not found"),
  SCRAP_ALREADY_EXIST(HttpStatus.NOT_ACCEPTABLE, "SCRAP002", "Scrap already exists"),

  // feed media
  IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "FMEDI001", "Image not found"),

  // comment
  COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMM001", "Comment not found"),

  // notification
  NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI001", "Notification not found"),
  NOTIFICATION_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTI001", "Notification setting not found"),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;

  GlobalErrorCode(final HttpStatus status, final String code, final String message) {
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

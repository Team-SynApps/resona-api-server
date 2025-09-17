package synapps.resona.api.member.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.SuccessCode;

public enum MemberSuccessCode implements SuccessCode {

  // Auth
  LOGIN_SUCCESS(HttpStatus.OK, "Login successful."),
  APPLE_LOGIN_SUCCESS(HttpStatus.OK, "Apple login successful."),
  TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "Token refreshed successfully."),
  MEMBER_INFO_SUCCESS(HttpStatus.OK, "Successfully retrieved member information."),

  // Follow
  FOLLOW_SUCCESS(HttpStatus.OK, "Follow successful."),
  UNFOLLOW_SUCCESS(HttpStatus.OK, "Unfollow successful."),
  GET_FOLLOWERS_SUCCESS(HttpStatus.OK, "Successfully retrieved follower list."),
  GET_FOLLOWINGS_SUCCESS(HttpStatus.OK, "Successfully retrieved following list."),

  // Member
  JOIN_SUCCESS(HttpStatus.CREATED, "Sign up successful."),
  GET_MY_INFO_SUCCESS(HttpStatus.OK, "Successfully retrieved my information."),
  GET_MEMBER_DETAIL_SUCCESS(HttpStatus.OK, "Successfully retrieved member details."),
  PASSWORD_CHANGE_SUCCESS(HttpStatus.OK, "Password changed successfully."),
  DELETE_USER_SUCCESS(HttpStatus.OK, "Member deleted successfully."),

  // Member Details
  REGISTER_DETAILS_SUCCESS(HttpStatus.CREATED, "Personal information registered successfully."),
  GET_DETAILS_SUCCESS(HttpStatus.OK, "Successfully retrieved personal information."),
  EDIT_DETAILS_SUCCESS(HttpStatus.OK, "Personal information updated successfully."),
  DELETE_DETAILS_SUCCESS(HttpStatus.OK, "Personal information deleted successfully."),

  // Notification
  REGISTER_FCM_TOKEN_SUCCESS(HttpStatus.OK, "FCM token registered successfully."),
  GET_NOTIFICATION_SETTING_SUCCESS(HttpStatus.OK, "Successfully retrieved notification settings."),
  UPDATE_NOTIFICATION_SETTING_SUCCESS(HttpStatus.OK, "Notification settings updated successfully."),
  GET_NOTIFICATIONS_SUCCESS(HttpStatus.OK, "Successfully retrieved notification list."),
  READ_NOTIFICATION_SUCCESS(HttpStatus.OK, "Notification read successfully."),
  DELETE_NOTIFICATION_SUCCESS(HttpStatus.OK, "Notification deleted successfully."),

  // Profile
  REGISTER_PROFILE_SUCCESS(HttpStatus.CREATED, "Profile registered successfully."),
  GET_PROFILE_SUCCESS(HttpStatus.OK, "Successfully retrieved profile."),
  EDIT_PROFILE_SUCCESS(HttpStatus.OK, "Profile updated successfully."),
  DELETE_PROFILE_SUCCESS(HttpStatus.OK, "Profile deleted successfully."),
  CHECK_TAG_DUPLICATE_SUCCESS(HttpStatus.OK, "Tag duplication check successful.");


  private final HttpStatus status;
  private final String message;

  MemberSuccessCode(HttpStatus status, String message) {
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
    return this.status.value();
  }
}
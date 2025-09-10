package synapps.resona.api.member.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.SuccessCode;

public enum MemberSuccessCode implements SuccessCode {

  // Auth
  LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공하였습니다."),
  APPLE_LOGIN_SUCCESS(HttpStatus.OK, "애플 로그인에 성공하였습니다."),
  TOKEN_REFRESH_SUCCESS(HttpStatus.OK, "토큰 재발급에 성공하였습니다."),
  MEMBER_INFO_SUCCESS(HttpStatus.OK, "회원 정보 조회에 성공하였습니다."),

  // Follow
  FOLLOW_SUCCESS(HttpStatus.OK, "팔로우에 성공하였습니다."),
  UNFOLLOW_SUCCESS(HttpStatus.OK, "언팔로우에 성공하였습니다."),
  GET_FOLLOWERS_SUCCESS(HttpStatus.OK, "팔로워 목록 조회에 성공하였습니다."),
  GET_FOLLOWINGS_SUCCESS(HttpStatus.OK, "팔로잉 목록 조회에 성공하였습니다."),

  // Member
  JOIN_SUCCESS(HttpStatus.CREATED, "회원가입에 성공하였습니다."),
  GET_MY_INFO_SUCCESS(HttpStatus.OK, "내 정보 조회에 성공하였습니다."),
  GET_MEMBER_DETAIL_SUCCESS(HttpStatus.OK, "회원 상세 정보 조회에 성공하였습니다."),
  PASSWORD_CHANGE_SUCCESS(HttpStatus.OK, "비밀번호 변경에 성공하였습니다."),
  DELETE_USER_SUCCESS(HttpStatus.OK, "회원 탈퇴에 성공하였습니다."),

  // Member Details
  REGISTER_DETAILS_SUCCESS(HttpStatus.CREATED, "개인정보 등록에 성공하였습니다."),
  GET_DETAILS_SUCCESS(HttpStatus.OK, "개인정보 조회에 성공하였습니다."),
  EDIT_DETAILS_SUCCESS(HttpStatus.OK, "개인정보 수정에 성공하였습니다."),
  DELETE_DETAILS_SUCCESS(HttpStatus.OK, "개인정보 삭제에 성공하였습니다."),

  // Notification
  REGISTER_FCM_TOKEN_SUCCESS(HttpStatus.OK, "FCM 토큰 등록에 성공하였습니다."),
  GET_NOTIFICATION_SETTING_SUCCESS(HttpStatus.OK, "알림 설정 조회에 성공하였습니다."),
  UPDATE_NOTIFICATION_SETTING_SUCCESS(HttpStatus.OK, "알림 설정 수정에 성공하였습니다."),
  GET_NOTIFICATIONS_SUCCESS(HttpStatus.OK, "알림 목록 조회에 성공하였습니다."),
  READ_NOTIFICATION_SUCCESS(HttpStatus.OK, "알림 읽음 처리에 성공하였습니다."),
  DELETE_NOTIFICATION_SUCCESS(HttpStatus.OK, "알림 삭제에 성공하였습니다."),

  // Profile
  REGISTER_PROFILE_SUCCESS(HttpStatus.CREATED, "프로필 등록에 성공하였습니다."),
  GET_PROFILE_SUCCESS(HttpStatus.OK, "프로필 조회에 성공하였습니다."),
  EDIT_PROFILE_SUCCESS(HttpStatus.OK, "프로필 수정에 성공하였습니다."),
  DELETE_PROFILE_SUCCESS(HttpStatus.OK, "프로필 삭제에 성공하였습니다."),
  CHECK_TAG_DUPLICATE_SUCCESS(HttpStatus.OK, "태그 중복 검사에 성공하였습니다.");


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
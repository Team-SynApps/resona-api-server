package synapps.resona.api.socialMedia.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.SuccessCode;

public enum SocialSuccessCode implements SuccessCode {

  // Feed
  REGISTER_FEED_SUCCESS(HttpStatus.CREATED, "피드 등록에 성공하였습니다."),
  GET_FEED_SUCCESS(HttpStatus.OK, "피드 조회에 성공하였습니다."),
  GET_FEEDS_SUCCESS(HttpStatus.OK, "피드 목록 조회에 성공하였습니다."),
  GET_MEMBER_FEEDS_SUCCESS(HttpStatus.OK, "사용자 피드 목록 조회에 성공하였습니다."),
  EDIT_FEED_SUCCESS(HttpStatus.OK, "피드 수정에 성공하였습니다."),
  DELETE_FEED_SUCCESS(HttpStatus.OK, "피드 삭제에 성공하였습니다."),

  // Like
  LIKE_SUCCESS(HttpStatus.OK, "좋아요 처리에 성공하였습니다."),
  UNLIKE_SUCCESS(HttpStatus.OK, "좋아요 취소에 성공하였습니다."),

  // Scrap
  SCRAP_SUCCESS(HttpStatus.OK, "스크랩에 성공하였습니다."),
  CANCEL_SCRAP_SUCCESS(HttpStatus.OK, "스크랩 취소에 성공하였습니다."),
  GET_SCRAP_SUCCESS(HttpStatus.OK, "스크랩 조회에 성공하였습니다."),
  GET_SCRAPS_SUCCESS(HttpStatus.OK, "스크랩 목록 조회에 성공하였습니다."),

  // Comment
  REGISTER_COMMENT_SUCCESS(HttpStatus.CREATED, "댓글 등록에 성공하였습니다."),
  EDIT_COMMENT_SUCCESS(HttpStatus.OK, "댓글 수정에 성공하였습니다."),
  DELETE_COMMENT_SUCCESS(HttpStatus.OK, "댓글 삭제에 성공하였습니다."),
  GET_COMMENT_SUCCESS(HttpStatus.OK, "댓글 조회에 성공하였습니다."),
  GET_COMMENTS_SUCCESS(HttpStatus.OK, "댓글 목록 조회에 성공하였습니다."),

  // Comment Like
  LIKE_COMMENT_SUCCESS(HttpStatus.OK, "댓글 좋아요 처리에 성공하였습니다."),
  UNLIKE_COMMENT_SUCCESS(HttpStatus.OK, "댓글 좋아요 취소에 성공하였습니다."),

  // Reply
  REGISTER_REPLY_SUCCESS(HttpStatus.CREATED, "답글 등록에 성공하였습니다."),
  GET_REPLY_SUCCESS(HttpStatus.OK, "답글 조회에 성공하였습니다."),
  GET_REPLIES_SUCCESS(HttpStatus.OK, "답글 목록 조회에 성공하였습니다."), // 답글 목록 조회 성공 코드 추가
  EDIT_REPLY_SUCCESS(HttpStatus.OK, "답글 수정에 성공하였습니다."),
  DELETE_REPLY_SUCCESS(HttpStatus.OK, "답글 삭제에 성공하였습니다."),

  // Report
  REPORT_FEED_SUCCESS(HttpStatus.OK, "피드 신고가 접수되었습니다."),
  REPORT_COMMENT_SUCCESS(HttpStatus.OK, "댓글 신고가 접수되었습니다."),
  REPORT_REPLY_SUCCESS(HttpStatus.OK, "대댓글 신고가 접수되었습니다."),

  // Mention
  REGISTER_MENTION_SUCCESS(HttpStatus.CREATED, "멘션 등록에 성공하였습니다."),
  GET_MENTION_SUCCESS(HttpStatus.OK, "멘션 조회에 성공하였습니다."),
  DELETE_MENTION_SUCCESS(HttpStatus.OK, "멘션 삭제에 성공하였습니다."),

  // Block
  BLOCK_SUCCESS(HttpStatus.CREATED, "사용자 차단에 성공하였습니다."),
  UNBLOCK_SUCCESS(HttpStatus.OK, "사용자 차단 해제에 성공하였습니다."),
  BLOCK_LIST_SUCCESS(HttpStatus.OK, "차단한 사용자 조회에 성공하였습니다."),

  // Hide
  HIDE_FEED_SUCCESS(HttpStatus.OK, "피드 숨김에 성공하였습니다."),
  HIDE_COMMENT_SUCCESS(HttpStatus.OK, "댓글 숨김에 성공하였습니다."),
  HIDE_REPLY_SUCCESS(HttpStatus.OK, "대댓글 숨김에 성공하였습니다.");


  private final HttpStatus status;
  private final String message;

  SocialSuccessCode(HttpStatus status, String message) {
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

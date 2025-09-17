package synapps.resona.api.socialMedia.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.SuccessCode;

public enum SocialSuccessCode implements SuccessCode {

  // Feed
  REGISTER_FEED_SUCCESS(HttpStatus.CREATED, "Feed registered successfully."),
  GET_FEED_SUCCESS(HttpStatus.OK, "Successfully retrieved feed."),
  GET_FEEDS_SUCCESS(HttpStatus.OK, "Successfully retrieved feed list."),
  GET_MEMBER_FEEDS_SUCCESS(HttpStatus.OK, "Successfully retrieved user feed list."),
  EDIT_FEED_SUCCESS(HttpStatus.OK, "Feed updated successfully."),
  DELETE_FEED_SUCCESS(HttpStatus.OK, "Feed deleted successfully."),

  // Feed Like
  LIKE_FEED_SUCCESS(HttpStatus.OK, "Feed liked successfully."),
  UNLIKE_FEED_SUCCESS(HttpStatus.OK, "Feed unliked successfully."),

  // Comment Like
  LIKE_COMMENT_SUCCESS(HttpStatus.OK, "Comment liked successfully."),
  UNLIKE_COMMENT_SUCCESS(HttpStatus.OK, "Comment unliked successfully."),

  // Reply Like
  LIKE_REPLY_SUCCESS(HttpStatus.OK, "Reply liked successfully."),
  UNLIKE_REPLY_SUCCESS(HttpStatus.OK, "Reply unliked successfully."),


  // Scrap
  SCRAP_SUCCESS(HttpStatus.OK, "Scrap successful."),
  CANCEL_SCRAP_SUCCESS(HttpStatus.OK, "Scrap canceled successfully."),
  GET_SCRAP_SUCCESS(HttpStatus.OK, "Successfully retrieved scrap."),
  GET_SCRAPS_SUCCESS(HttpStatus.OK, "Successfully retrieved scrap list."),

  // Comment
  REGISTER_COMMENT_SUCCESS(HttpStatus.CREATED, "Comment registered successfully."),
  EDIT_COMMENT_SUCCESS(HttpStatus.OK, "Comment updated successfully."),
  DELETE_COMMENT_SUCCESS(HttpStatus.OK, "Comment deleted successfully."),
  GET_COMMENT_SUCCESS(HttpStatus.OK, "Successfully retrieved comment."),
  GET_COMMENTS_SUCCESS(HttpStatus.OK, "Successfully retrieved comment list."),

  // Reply
  REGISTER_REPLY_SUCCESS(HttpStatus.CREATED, "Reply registered successfully."),
  GET_REPLY_SUCCESS(HttpStatus.OK, "Successfully retrieved reply."),
  GET_REPLIES_SUCCESS(HttpStatus.OK, "Successfully retrieved reply list."),
  EDIT_REPLY_SUCCESS(HttpStatus.OK, "Reply updated successfully."),
  DELETE_REPLY_SUCCESS(HttpStatus.OK, "Reply deleted successfully."),

  // Report
  REPORT_FEED_SUCCESS(HttpStatus.OK, "Feed reported successfully."),
  REPORT_COMMENT_SUCCESS(HttpStatus.OK, "Comment reported successfully."),
  REPORT_REPLY_SUCCESS(HttpStatus.OK, "Reply reported successfully."),

  // Mention
  REGISTER_MENTION_SUCCESS(HttpStatus.CREATED, "Mention registered successfully."),
  GET_MENTION_SUCCESS(HttpStatus.OK, "Successfully retrieved mention."),
  DELETE_MENTION_SUCCESS(HttpStatus.OK, "Mention deleted successfully."),

  // Block
  BLOCK_SUCCESS(HttpStatus.CREATED, "User blocked successfully."),
  UNBLOCK_SUCCESS(HttpStatus.OK, "User unblocked successfully."),
  BLOCK_LIST_SUCCESS(HttpStatus.OK, "Successfully retrieved blocked user list."),

  // Hide
  HIDE_FEED_SUCCESS(HttpStatus.OK, "Feed hidden successfully."),
  HIDE_COMMENT_SUCCESS(HttpStatus.OK, "Comment hidden successfully."),
  HIDE_REPLY_SUCCESS(HttpStatus.OK, "Reply hidden successfully.");


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

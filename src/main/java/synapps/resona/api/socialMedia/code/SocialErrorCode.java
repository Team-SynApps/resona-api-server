package synapps.resona.api.socialMedia.code;

import org.springframework.http.HttpStatus;
import synapps.resona.api.global.dto.code.ErrorCode;

public enum SocialErrorCode implements ErrorCode {
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

  // block
  CANNOT_BLOCK_SELF(HttpStatus.CONFLICT, "BLK001", "Cannot block self"),
  ALREADY_BLOCKED(HttpStatus.NOT_ACCEPTABLE, "BLK002", "Already blocked"),
  NOT_BLOCKED(HttpStatus.BAD_REQUEST, "BLK003", "Not blocked"),
  ;

  private final String code;
  private final String message;
  private final HttpStatus status;

  SocialErrorCode(final HttpStatus status, final String code, final String message) {
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

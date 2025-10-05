package com.synapps.resona.code;

import com.synapps.resona.dto.code.ErrorCode;
import org.springframework.http.HttpStatus;

public enum SocialErrorCode implements ErrorCode {
  // feed
  FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "FEED001", "Feed not found"),

  // mention
  MENTION_NOT_FOUND(HttpStatus.NOT_FOUND, "MENT001", "Mention not found"),

  // like
  LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE001", "Like not found"),
  ALREADY_LIKED(HttpStatus.CONFLICT, "LIKE002", "already liked"),

  // reply
  REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "REPLY001", "Reply not found"),

  // scrap
  SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "SCRAP001", "Scrap not found"),
  SCRAP_ALREADY_EXIST(HttpStatus.NOT_ACCEPTABLE, "SCRAP002", "Scrap already exists"),

  // feed media
  IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "FMEDI001", "Image not found"),

  // comment
  COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMM001", "Comment not found"),

  // report
  REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPO001", "Report not found"),
  ALREADY_REPORTED(HttpStatus.CONFLICT, "REPO002", "Already reported"),
  ALREADY_EXECUTED(HttpStatus.CONFLICT, "REPO002", "Already executed"),

  // General
  FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH100", "Forbidden access"),
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

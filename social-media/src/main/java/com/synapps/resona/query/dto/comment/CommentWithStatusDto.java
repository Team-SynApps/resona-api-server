package com.synapps.resona.query.dto.comment;

import com.synapps.resona.domain.entity.comment.Comment;
import lombok.Getter;

@Getter
public class CommentWithStatusDto {
  private final Comment comment;
  private final boolean isBlocked;
  private final boolean isHidden;

  public CommentWithStatusDto(Comment comment, boolean isBlocked, boolean isHidden) {
    this.comment = comment;
    this.isBlocked = isBlocked;
    this.isHidden = isHidden;
  }
}
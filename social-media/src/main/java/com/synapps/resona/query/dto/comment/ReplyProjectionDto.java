package com.synapps.resona.query.dto.comment;

import com.synapps.resona.domain.entity.comment.Reply;
import lombok.Getter;

@Getter
public class ReplyProjectionDto {
  private final Reply reply;
  private final boolean isBlocked;
  private final boolean isHidden;

  public ReplyProjectionDto(Reply reply, boolean isBlocked, boolean isHidden) {
    this.reply = reply;
    this.isBlocked = isBlocked;
    this.isHidden = isHidden;
  }
}
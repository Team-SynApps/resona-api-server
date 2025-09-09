package synapps.resona.api.socialMedia.dto.comment;

import lombok.Getter;
import synapps.resona.api.socialMedia.entity.comment.Reply;

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
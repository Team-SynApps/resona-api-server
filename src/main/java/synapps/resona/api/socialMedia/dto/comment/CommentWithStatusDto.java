package synapps.resona.api.socialMedia.dto.comment;

import lombok.Getter;
import synapps.resona.api.socialMedia.entity.comment.Comment;

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
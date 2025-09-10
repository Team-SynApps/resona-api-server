package synapps.resona.api.socialMedia.dto.comment;

import java.util.List;
import lombok.Getter;
import synapps.resona.api.socialMedia.entity.comment.Comment;

@Getter
public class CommentProjectionDto {
  private final Comment comment;
  private final boolean isBlocked;
  private final boolean isHidden;
  private final Long likeCount;
  private final List<ReplyProjectionDto> replies;

  public CommentProjectionDto(Comment comment, boolean isBlocked, boolean isHidden,long likeCount, List<ReplyProjectionDto> replies) {
    this.comment = comment;
    this.isBlocked = isBlocked;
    this.isHidden = isHidden;
    this.likeCount = likeCount;
    this.replies = replies;
  }
}
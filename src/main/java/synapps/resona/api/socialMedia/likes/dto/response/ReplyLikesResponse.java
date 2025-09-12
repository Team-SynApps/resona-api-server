package synapps.resona.api.socialMedia.likes.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ReplyLikesResponse extends LikesResponse {
  private final Long replyId;

  public static ReplyLikesResponse of(Long replyId, long likesCount, boolean isLiked) {
    return ReplyLikesResponse.builder()
        .replyId(replyId)
        .likesCount(likesCount)
        .isLiked(isLiked)
        .build();
  }
}

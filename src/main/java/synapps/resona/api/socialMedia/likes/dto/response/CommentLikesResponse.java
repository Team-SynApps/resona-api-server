package synapps.resona.api.socialMedia.likes.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Schema(description = "댓글 좋아요 응답 DTO")
@Getter
@SuperBuilder
public class CommentLikesResponse extends LikesResponse {
  private final Long commentId;

  public static CommentLikesResponse of(Long commentId, long likesCount, boolean isLiked) {
    return CommentLikesResponse.builder()
        .commentId(commentId)
        .likesCount(likesCount)
        .isLiked(isLiked)
        .build();
  }
}
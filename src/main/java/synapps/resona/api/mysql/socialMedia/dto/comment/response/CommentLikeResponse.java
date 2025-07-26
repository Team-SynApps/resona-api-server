package synapps.resona.api.mysql.socialMedia.dto.comment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.comment.CommentLikes;

@Schema(description = "댓글 좋아요 응답 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class CommentLikeResponse {

  @Schema(description = "댓글 좋아요 고유 ID", example = "1")
  private Long commentLikeId;

  @Schema(description = "좋아요를 누른 회원 ID", example = "101")
  private Long memberId;

  @Schema(description = "좋아요가 눌린 댓글 ID", example = "303")
  private Long commentId;

  @Schema(description = "좋아요 생성 시각")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  /**
   * CommentLikes 엔티티를 CommentLikeResponseDto로 변환하는 정적 팩토리 메서드
   * @param commentLikes CommentLikes 엔티티 객체
   * @return 생성된 CommentLikeResponseDto
   */
  public static CommentLikeResponse from(CommentLikes commentLikes) {
    return CommentLikeResponse.of(
        commentLikes.getId(),
        commentLikes.getMember().getId(),
        commentLikes.getComment().getId(),
        commentLikes.getCreatedAt()
    );
  }
}
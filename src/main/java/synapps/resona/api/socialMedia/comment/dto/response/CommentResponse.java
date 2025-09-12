package synapps.resona.api.socialMedia.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.comment.entity.Comment;

@Schema(description = "댓글 수정 응답 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class CommentResponse {

  @Schema(description = "수정된 댓글의 ID", example = "1")
  private Long commentId;

  @Schema(description = "수정된 댓글 내용", example = "이 내용으로 수정되었습니다.")
  private String content;

  @Schema(description = "댓글 생성 시각")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @Schema(description = "댓글 최종 수정 시각")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime modifiedAt;

  /**
   * Comment 엔티티를 CommentResponse DTO로 변환하는 정적 팩토리 메서드
   * @param comment Comment 엔티티 객체
   * @return 생성된 CommentResponse DTO
   */
  public static CommentResponse from(Comment comment) {
    return CommentResponse.of(
        comment.getId(),
        comment.getContent(),
        comment.getCreatedAt(),
        comment.getModifiedAt()
    );
  }
}

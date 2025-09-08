package synapps.resona.api.socialMedia.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.dto.reply.ReplyDto;
import synapps.resona.api.socialMedia.entity.comment.Comment;

@Schema(description = "댓글 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class CommentDto {
  @Schema(description = "댓글의 ID", example = "1")
  private Long commentId;

  @Schema(description = "댓글 내용", example = "이 내용으로 수정되었습니다.")
  private String content;

  @Schema(description = "댓글 생성 시각")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @Schema(description = "댓글 최종 수정 시각")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime modifiedAt;

  private List<ReplyDto> replies = new ArrayList<>();

  public static CommentDto from(Comment comment) {
    return CommentDto.of(
        comment.getId(),
        comment.getContent(),
        comment.getCreatedAt(),
        comment.getModifiedAt(),
        comment.getReplies().stream().map(ReplyDto::from).toList()
    );
  }
}

package synapps.resona.api.socialMedia.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.feed.dto.SocialMemberDto;
import synapps.resona.api.socialMedia.comment.entity.Comment;
import synapps.resona.api.socialMedia.comment.entity.ContentDisplayStatus;

@Schema(description = "댓글 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDto {
  @Schema(description = "댓글의 ID", example = "1")
  private Long commentId;

  @Schema(description = "댓글 작성자", example = "test user")
  private SocialMemberDto author;

  @Schema(description = "댓글 내용", example = "댓글 내용 예시입니다.")
  private String content;

  @Schema(description = "댓글 좋아요 수", example = "3")
  private long likeCount;

  @Schema(description = "댓글 상태", example = "BLOCKED")
  private ContentDisplayStatus status;

  @Schema(description = "댓글 생성 시각")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @Schema(description = "댓글 최종 수정 시각")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime modifiedAt;

  private List<ReplyDto> replies = new ArrayList<>();

  public static CommentDto of(Comment comment, ContentDisplayStatus status, String content, long likeCount, List<ReplyDto> processedReplies) {
    return CommentDto.builder()
        .commentId(comment.getId())
        .author(SocialMemberDto.from(comment.getMember()))
        .likeCount(likeCount)
        .content(content)
        .status(status)
        .createdAt(comment.getCreatedAt())
        .modifiedAt(comment.getModifiedAt())
        .replies(processedReplies)
        .build();
  }

  // reply 없음
  public static CommentDto of(Comment comment) {
    return CommentDto.builder()
        .commentId(comment.getId())
        .author(SocialMemberDto.from(comment.getMember()))
        .content(comment.getContent())
        .status(ContentDisplayStatus.NORMAL)
        .createdAt(comment.getCreatedAt())
        .modifiedAt(comment.getModifiedAt())
        .build();
  }
}

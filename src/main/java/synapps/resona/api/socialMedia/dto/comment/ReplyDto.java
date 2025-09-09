package synapps.resona.api.socialMedia.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.dto.feed.SocialMemberDto;
import synapps.resona.api.socialMedia.entity.comment.ContentDisplayStatus;
import synapps.resona.api.socialMedia.entity.comment.Reply;

@Schema(description = "답글 응답 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReplyDto {

  @Schema(description = "답글이 달린 원본 댓글의 ID", example = "202")
  private Long commentId;

  @Schema(description = "생성된 답글의 ID", example = "1")
  private Long replyId;

  @Schema(description = "대댓글 작성자", example = "test user")
  private SocialMemberDto author;

  @Schema(description = "대댓글 상태", example = "NORMAL")
  private ContentDisplayStatus status;

  @Schema(description = "답글 내용", example = "이것은 답글입니다.")
  private String content;

  @Schema(description = "답글 생성 시각")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  public static ReplyDto of(Reply reply, ContentDisplayStatus status, String content) {
    return ReplyDto.builder()
        .commentId(reply.getComment().getId())
        .replyId(reply.getId())
        .author(SocialMemberDto.from(reply.getMember()))
        .status(status)
        .content(content)
        .createdAt(reply.getCreatedAt())
        .build();
  }
}

package synapps.resona.api.socialMedia.dto.reply;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.dto.feed.SocialMemberDto;
import synapps.resona.api.socialMedia.dto.reply.response.ReplyResponse;
import synapps.resona.api.socialMedia.entity.comment.Reply;

@Schema(description = "답글 응답 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class ReplyDto {

  @Schema(description = "답글이 달린 원본 댓글의 ID", example = "202")
  private Long commentId;

  @Schema(description = "생성된 답글의 ID", example = "1")
  private Long replyId;

  private SocialMemberDto author;

  @Schema(description = "답글 내용", example = "이것은 답글입니다.")
  private String content;

  @Schema(description = "답글 생성 시각")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  public static ReplyDto from(Reply reply) {
    return ReplyDto.of(
        reply.getComment().getId(),
        reply.getId(),
        SocialMemberDto.from(reply.getMember()),
        reply.getContent(),
        reply.getCreatedAt());
  }
}

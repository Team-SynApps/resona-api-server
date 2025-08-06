package synapps.resona.api.mysql.socialMedia.dto.reply.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.comment.Reply;

@Schema(description = "답글 응답 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class ReplyResponse {

  @Schema(description = "답글이 달린 원본 댓글의 ID", example = "202")
  private Long commentId;

  @Schema(description = "생성된 답글의 ID", example = "1")
  private Long replyId;

  @Schema(description = "답글 내용", example = "이것은 답글입니다.")
  private String content;

  @Schema(description = "답글 생성 시각")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  /**
   * Reply 엔티티를 ReplyPostResponse DTO로 변환하는 정적 팩토리 메서드
   * @param reply Reply 엔티티 객체
   * @param commentId 원본 댓글의 ID
   * @return 생성된 ReplyPostResponse DTO
   */
  public static ReplyResponse from(Reply reply, Long commentId) {
    return ReplyResponse.of(
        commentId,
        reply.getId(),
        reply.getContent(),
        reply.getCreatedAt()
    );
  }

  public static ReplyResponse from(Reply reply) {
    return ReplyResponse.of(
        reply.getComment().getId(),
        reply.getId(),
        reply.getContent(),
        reply.getCreatedAt());
  }
}

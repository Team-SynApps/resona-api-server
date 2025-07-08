package synapps.resona.api.mysql.socialMedia.dto.reply.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.comment.Reply;

@Data
@Builder
public class ReplyReadResponse {

  private String commentId;
  private String replyId;
  private String content;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime createdAt;

  public static ReplyReadResponse from(Reply reply) {
    return ReplyReadResponse.builder()
        .commentId(reply.getComment().getId().toString())
        .replyId(reply.getId().toString())
        .content(reply.getContent())
        .build();
  }
}
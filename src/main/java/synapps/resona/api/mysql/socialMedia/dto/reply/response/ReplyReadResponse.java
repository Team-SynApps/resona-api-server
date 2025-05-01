package synapps.resona.api.mysql.socialMedia.dto.reply.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.comment.Reply;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyReadResponse {

  private Long commentId;
  private Long replyId;
  private String content;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime createdAt;

  public ReplyReadResponse(Reply reply, Long commentId) {
    this.commentId = commentId;
    this.replyId = reply.getId();
    this.content = reply.getContent();
  }
}
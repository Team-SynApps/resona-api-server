package synapps.resona.api.mysql.socialMedia.dto.reply.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.comment.Reply;

@Data
@Builder
public class ReplyPostResponse {

  private String commentId;
  private String replyId;
  private String content;
  private String createdAt;

  public static ReplyPostResponse from(Reply reply, Long commentId) {
    return ReplyPostResponse.builder()
        .commentId(commentId.toString())
        .replyId(reply.getId().toString())
        .content(reply.getContent())
        .createdAt(reply.getCreatedAt().toString())
        .build();
  }
}

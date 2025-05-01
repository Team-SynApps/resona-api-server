package synapps.resona.api.mysql.socialMedia.dto.comment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReadResponse {

  private Long commentId;
  private String content;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime createdAt;

  public CommentReadResponse(Comment comment) {
    this.commentId = comment.getId();
    this.content = comment.getContent();
    this.createdAt = comment.getCreatedAt();
  }
}
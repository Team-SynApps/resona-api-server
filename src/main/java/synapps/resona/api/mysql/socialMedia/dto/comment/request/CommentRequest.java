package synapps.resona.api.mysql.socialMedia.dto.comment.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

  private Long feedId;
  private String content;
}

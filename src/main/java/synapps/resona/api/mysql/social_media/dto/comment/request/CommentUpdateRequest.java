package synapps.resona.api.mysql.social_media.dto.comment.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {
    private Long commentId;
    private String content;
}

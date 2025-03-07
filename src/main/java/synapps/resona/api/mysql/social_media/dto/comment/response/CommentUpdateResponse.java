package synapps.resona.api.mysql.social_media.dto.comment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateResponse {
    private String commentId;
    private String content;
    private String createdAt;
    private String modifiedAt;
}

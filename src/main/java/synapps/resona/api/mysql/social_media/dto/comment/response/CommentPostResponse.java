package synapps.resona.api.mysql.social_media.dto.comment.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentPostResponse {
    private String commentId;
    private String content;
    private String createdAt;
}

package synapps.resona.api.mysql.social_media.dto.reply.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyPostResponse {
    private String commentId;
    private String replyId;
    private String content;
    private String createdAt;
}

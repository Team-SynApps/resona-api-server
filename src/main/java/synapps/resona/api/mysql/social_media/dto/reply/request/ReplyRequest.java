package synapps.resona.api.mysql.social_media.dto.reply.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyRequest {
    private Long commentId;
    private String content;
}

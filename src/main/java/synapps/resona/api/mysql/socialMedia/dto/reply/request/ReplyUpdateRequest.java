package synapps.resona.api.mysql.socialMedia.dto.reply.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyUpdateRequest {
    private Long replyId;
    private String content;
}

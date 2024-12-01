package synapps.resona.api.mysql.social_media.dto.reply;

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

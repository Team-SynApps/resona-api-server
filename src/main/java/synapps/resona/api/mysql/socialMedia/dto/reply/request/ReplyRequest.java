package synapps.resona.api.mysql.socialMedia.dto.reply.request;

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

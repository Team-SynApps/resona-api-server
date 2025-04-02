package synapps.resona.api.mysql.social_media.dto.reply.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.social_media.entity.Comment;
import synapps.resona.api.mysql.social_media.entity.Reply;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyReadResponse {
    private Long commentId;
    private Long replyId;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    public ReplyReadResponse(Reply reply, Long commentId) {
        this.commentId = commentId;
        this.replyId = reply.getId();
        this.content = reply.getContent();
    }
}
package synapps.resona.api.mysql.socialMedia.repository.comment.reply;

import java.util.List;
import synapps.resona.api.mysql.socialMedia.entity.comment.Reply;

public interface ReplyRepositoryCustom {
  List<Reply> findAllRepliesByCommentId(Long viewerId, Long commentId);
}

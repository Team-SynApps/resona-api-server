package synapps.resona.api.socialMedia.comment.repository.reply;

import java.util.List;
import synapps.resona.api.socialMedia.comment.entity.Reply;

public interface ReplyRepositoryCustom {
  List<Reply> findAllRepliesByCommentId(Long viewerId, Long commentId);
}

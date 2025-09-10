package synapps.resona.api.socialMedia.repository.comment.reply;

import java.util.List;
import synapps.resona.api.socialMedia.entity.comment.Reply;

public interface ReplyRepositoryCustom {
  List<Reply> findAllRepliesByCommentId(Long viewerId, Long commentId);
}

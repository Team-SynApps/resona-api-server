package synapps.resona.api.mysql.socialMedia.repository.comment;

import java.util.List;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;

public interface CommentRepositoryCustom {
  List<Comment> findAllCommentsByFeedIdWithReplies(Long viewerId, Long feedId);
}

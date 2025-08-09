package synapps.resona.api.socialMedia.repository.comment;

import java.util.List;
import synapps.resona.api.socialMedia.entity.comment.Comment;

public interface CommentRepositoryCustom {
  List<Comment> findAllCommentsByFeedIdWithReplies(Long viewerId, Long feedId);
}

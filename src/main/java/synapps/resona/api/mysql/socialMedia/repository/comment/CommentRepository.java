package synapps.resona.api.mysql.socialMedia.repository.comment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  // for test
  List<Comment> findAllByFeed(Feed feed);

  boolean existsByIdAndMember(Long commentId, Member member);

  @Query("SELECT c FROM Comment c " +
      "WHERE c.feed.id = :feedId")
  List<Comment> findAllCommentsByFeedId(@Param("feedId") Long feedId);

  @Query("SELECT DISTINCT c FROM Comment c " +
      "LEFT JOIN FETCH c.replies " +
      "WHERE c.id = :commentId")
  Optional<Comment> findWithReplies(@Param("commentId") Long commentId);
}

package synapps.resona.api.socialMedia.comment.repository.reply;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.comment.entity.Comment;
import synapps.resona.api.socialMedia.comment.entity.Reply;

@MySQLRepository
public interface ReplyRepository extends JpaRepository<Reply, Long> , ReplyRepositoryCustom {

  // for test
  List<Reply> findAllByComment(Comment comment);

  boolean existsByIdAndMember(Long replyId, Member member);

  @Query("SELECT r FROM Reply r " +
      "JOIN FETCH r.comment " +
      "JOIN FETCH r.member " +
      "WHERE r.id = :replyId")
  Optional<Reply> findWithCommentAndMemberById(@Param("replyId") Long replyId);

  @Query("SELECT r FROM Reply r " +
      "WHERE r.comment.id = :commentId")
  List<Reply> findAllRepliesByCommnetId(@Param("commentId") Long commentId);
}

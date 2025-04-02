package synapps.resona.api.mysql.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.social_media.entity.Comment;
import synapps.resona.api.mysql.social_media.entity.Reply;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // for test
    List<Reply> findAllByComment(Comment comment);

    boolean existsByIdAndMember(Long replyId, Member member);

    @Query("SELECT r FROM Reply r " +
            "JOIN FETCH r.comment " +
            "WHERE r.id = :replyId")
    Optional<Reply> findWithCommentById(@Param("replyId") Long replyId);

    @Query("SELECT r FROM Reply r " +
            "WHERE r.comment.id = :commentId")
    List<Reply> findAllRepliesByCommnetId(@Param("commentId") Long commentId);
}

package com.synapps.resona.domain.repository.comment.reply;

import com.synapps.resona.domain.entity.comment.Comment;
import com.synapps.resona.domain.entity.comment.Reply;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

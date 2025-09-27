package com.synapps.resona.domain.repository.comment.comment;

import com.synapps.resona.domain.entity.comment.Comment;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.domain.entity.feed.Feed;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@MySQLRepository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

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

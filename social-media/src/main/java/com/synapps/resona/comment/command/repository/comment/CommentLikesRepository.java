package com.synapps.resona.comment.command.repository.comment;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.comment.command.entity.comment.CommentLikes;
import com.synapps.resona.entity.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@MySQLRepository
public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {
  @Modifying
  @Query("UPDATE CommentLikes cl SET cl.deleted = true WHERE cl.comment = :comment")
  void softDeleteAllByComment(@Param("comment") Comment comment);

  Optional<CommentLikes> findByMemberAndComment(Member member, Comment comment);
}
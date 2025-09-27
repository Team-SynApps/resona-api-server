package com.synapps.resona.domain.repository.likes;

import com.synapps.resona.domain.entity.comment.Comment;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.domain.entity.likes.CommentLikes;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {

  boolean existsByCommentAndMember(Comment comment, Member member);

  Optional<CommentLikes> findByCommentAndMember(Comment comment, Member member);

  long countByCommentId(Long commentId);
}
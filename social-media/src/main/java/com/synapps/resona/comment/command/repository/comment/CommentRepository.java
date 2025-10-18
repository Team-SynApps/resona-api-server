package com.synapps.resona.comment.command.repository.comment;

import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  boolean existsByIdAndMember(Long commentId, Member member);

}

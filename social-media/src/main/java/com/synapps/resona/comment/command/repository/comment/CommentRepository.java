package com.synapps.resona.comment.command.repository.comment;

import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@MySQLRepository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  boolean existsByIdAndMember(Long commentId, Member member);

}

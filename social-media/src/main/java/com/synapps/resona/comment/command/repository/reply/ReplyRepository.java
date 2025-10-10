package com.synapps.resona.comment.command.repository.reply;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.entity.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

  List<Reply> findAllByCommentAndDeletedFalse(Comment comment);

  boolean existsByIdAndMember(Long replyId, Member member);
}

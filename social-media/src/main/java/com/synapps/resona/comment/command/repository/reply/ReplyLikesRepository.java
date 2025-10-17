package com.synapps.resona.comment.command.repository.reply;

import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.comment.command.entity.reply.ReplyLikes;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@MySQLRepository
public interface ReplyLikesRepository extends JpaRepository<ReplyLikes, Long> {

  boolean existsByReplyAndMember(Reply reply, Member member);

  Optional<ReplyLikes> findByMemberAndReply(Member member, Reply reply);

  @Modifying
  @Query("UPDATE ReplyLikes rl SET rl.deleted = true WHERE rl.reply = :reply")
  void softDeleteAllByReply(@Param("reply") Reply reply);
}
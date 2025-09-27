package com.synapps.resona.likes.repository;

import com.synapps.resona.comment.entity.Reply;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.likes.entity.ReplyLikes;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface ReplyLikesRepository extends JpaRepository<ReplyLikes, Long> {

  boolean existsByReplyAndMember(Reply reply, Member member);

  Optional<ReplyLikes> findByReplyAndMember(Reply reply, Member member);

  long countByReplyId(Long replyId);
}
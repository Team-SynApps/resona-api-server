package com.synapps.resona.domain.repository.likes;

import com.synapps.resona.domain.entity.comment.Reply;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.domain.entity.likes.ReplyLikes;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface ReplyLikesRepository extends JpaRepository<ReplyLikes, Long> {

  boolean existsByReplyAndMember(Reply reply, Member member);

  Optional<ReplyLikes> findByReplyAndMember(Reply reply, Member member);

  long countByReplyId(Long replyId);
}
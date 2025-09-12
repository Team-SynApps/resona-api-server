package synapps.resona.api.socialMedia.likes.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.comment.entity.Reply;
import synapps.resona.api.socialMedia.likes.entity.ReplyLikes;

@MySQLRepository
public interface ReplyLikesRepository extends JpaRepository<ReplyLikes, Long> {

  boolean existsByReplyAndMember(Reply reply, Member member);

  Optional<ReplyLikes> findByReplyAndMember(Reply reply, Member member);

  long countByReplyId(Long replyId);
}
package synapps.resona.api.socialMedia.repository.comment.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.entity.comment.CommentLikes;

@MySQLRepository
public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {

  boolean existsByIdAndMember(Long commentLikesId, Member member);
}
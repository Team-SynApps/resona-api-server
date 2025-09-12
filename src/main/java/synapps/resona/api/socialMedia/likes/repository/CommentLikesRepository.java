package synapps.resona.api.socialMedia.likes.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.comment.entity.Comment;
import synapps.resona.api.socialMedia.likes.entity.CommentLikes;

@MySQLRepository
public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {

  boolean existsByCommentAndMember(Comment comment, Member member);

  Optional<CommentLikes> findByCommentAndMember(Comment comment, Member member);

  long countByCommentId(Long commentId);
}
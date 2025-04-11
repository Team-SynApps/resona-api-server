package synapps.resona.api.mysql.socialMedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.socialMedia.entity.comment.CommentLikes;

@Repository
public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {
}
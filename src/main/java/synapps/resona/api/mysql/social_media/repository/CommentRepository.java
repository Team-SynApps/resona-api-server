package synapps.resona.api.mysql.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.mysql.social_media.entity.Comment;
import synapps.resona.api.mysql.social_media.entity.Feed;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // for test
    List<Comment> findAllByFeed(Feed feed);
}

package synapps.resona.api.mysql.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.mysql.social_media.entity.Feed;

public interface FeedRepository extends JpaRepository<Feed, Long> {
}

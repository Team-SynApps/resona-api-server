package synapps.resona.api.mysql.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.social_media.entity.FeedMedia;

@Repository
public interface FeedMediaRepository extends JpaRepository<FeedMedia, Long> {
}

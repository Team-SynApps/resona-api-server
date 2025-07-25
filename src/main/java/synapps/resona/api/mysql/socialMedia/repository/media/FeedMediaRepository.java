package synapps.resona.api.mysql.socialMedia.repository.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.socialMedia.entity.media.FeedMedia;

@Repository
public interface FeedMediaRepository extends JpaRepository<FeedMedia, Long> {

}

package synapps.resona.api.socialMedia.repository.media;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.socialMedia.entity.media.FeedMedia;

@MySQLRepository
public interface FeedMediaRepository extends JpaRepository<FeedMedia, Long> {

}

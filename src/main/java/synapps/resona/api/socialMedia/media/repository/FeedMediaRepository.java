package synapps.resona.api.socialMedia.media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.socialMedia.media.entity.FeedMedia;

@MySQLRepository
public interface FeedMediaRepository extends JpaRepository<FeedMedia, Long> {

}

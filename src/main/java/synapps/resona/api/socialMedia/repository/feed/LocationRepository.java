package synapps.resona.api.socialMedia.repository.feed;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.socialMedia.entity.feed.Location;

@MySQLRepository
public interface LocationRepository extends JpaRepository<Location, Long> {

}

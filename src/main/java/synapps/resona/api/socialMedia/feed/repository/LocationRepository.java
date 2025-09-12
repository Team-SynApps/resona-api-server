package synapps.resona.api.socialMedia.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.socialMedia.feed.entity.Location;

@MySQLRepository
public interface LocationRepository extends JpaRepository<Location, Long> {

}

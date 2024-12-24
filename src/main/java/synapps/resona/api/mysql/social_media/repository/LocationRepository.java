package synapps.resona.api.mysql.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.social_media.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}

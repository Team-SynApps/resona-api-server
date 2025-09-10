package synapps.resona.api.socialMedia.repository.restriction;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.socialMedia.entity.restriction.Hide;

@MySQLRepository
public interface HideRepository extends JpaRepository<Hide, Long> {

}

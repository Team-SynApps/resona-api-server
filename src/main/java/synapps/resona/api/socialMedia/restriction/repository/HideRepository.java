package synapps.resona.api.socialMedia.restriction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.socialMedia.restriction.entity.Hide;

@MySQLRepository
public interface HideRepository extends JpaRepository<Hide, Long> {

}

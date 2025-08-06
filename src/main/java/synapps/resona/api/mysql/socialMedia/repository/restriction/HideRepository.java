package synapps.resona.api.mysql.socialMedia.repository.restriction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.socialMedia.entity.restriction.Hide;

@Repository
public interface HideRepository extends JpaRepository<Hide, Long> {

}

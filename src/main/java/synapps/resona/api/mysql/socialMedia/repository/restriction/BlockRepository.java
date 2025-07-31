package synapps.resona.api.mysql.socialMedia.repository.restriction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.socialMedia.entity.restriction.Block;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

}

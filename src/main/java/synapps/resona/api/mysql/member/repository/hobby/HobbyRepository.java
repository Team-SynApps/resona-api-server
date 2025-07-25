package synapps.resona.api.mysql.member.repository.hobby;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.hobby.Hobby;

@Repository
public interface HobbyRepository extends JpaRepository<Hobby, Long> {

  Optional<Hobby> findByMemberDetailsIdAndName(Long memberDetailsId, String name);
}

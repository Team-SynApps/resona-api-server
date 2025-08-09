package synapps.resona.api.member.repository.hobby;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.hobby.Hobby;

@MySQLRepository
public interface HobbyRepository extends JpaRepository<Hobby, Long> {

  Optional<Hobby> findByMemberDetailsIdAndName(Long memberDetailsId, String name);
}

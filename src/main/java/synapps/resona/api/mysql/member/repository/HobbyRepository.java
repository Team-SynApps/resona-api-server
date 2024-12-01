package synapps.resona.api.mysql.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.mysql.member.entity.interests.Hobby;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
}

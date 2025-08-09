package synapps.resona.api.member.repository.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.profile.Profile;

@MySQLRepository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

  boolean existsByTag(String tag);
}

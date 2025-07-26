package synapps.resona.api.mysql.member.repository.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.profile.Profile;


@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

  boolean existsByTag(String tag);
}

package synapps.resona.api.mysql.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.profile.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByMember(Member member);
}

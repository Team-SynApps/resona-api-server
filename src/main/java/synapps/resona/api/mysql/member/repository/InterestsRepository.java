package synapps.resona.api.mysql.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.mysql.member.entity.Member;
import synapps.resona.api.mysql.member.entity.interests.Interests;

import java.util.Optional;

public interface InterestsRepository extends JpaRepository<Interests, Long> {
    Optional<Interests> findByMember(Member member);
}

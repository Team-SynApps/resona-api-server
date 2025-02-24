package synapps.resona.api.mysql.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.mysql.member.entity.interests.Interests;
import synapps.resona.api.mysql.member.entity.member.Member;

import java.util.Optional;

public interface InterestsRepository extends JpaRepository<Interests, Long> {
    Optional<Interests> findByMember(Member member);
}

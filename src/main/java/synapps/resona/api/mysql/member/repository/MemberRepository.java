package synapps.resona.api.mysql.member.repository;

import synapps.resona.api.mysql.member.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
    Optional<Member> findById(Long id);
}

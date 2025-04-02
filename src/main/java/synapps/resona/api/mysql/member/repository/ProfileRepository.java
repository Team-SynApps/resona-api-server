package synapps.resona.api.mysql.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.profile.Profile;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByMember(Member member);

    Optional<Profile> findByMemberId(Long memberId);

    @Query("SELECT p FROM Profile p JOIN FETCH p.member m WHERE m.email = :email")
    Optional<Profile> findByMemberEmail(@Param("email") String email);
}

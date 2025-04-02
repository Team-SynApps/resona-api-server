package synapps.resona.api.mysql.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;

import java.util.Optional;

@Repository
public interface MemberDetailsRepository extends JpaRepository<MemberDetails, Long> {
    Optional<MemberDetails> findByMember(Member member);
    Optional<MemberDetails> findByMemberId(Long memberId);

    @Query("SELECT md FROM MemberDetails md JOIN FETCH md.member m WHERE m.email = :email")
    Optional<MemberDetails> findByMemberEmail(@Param("email") String email);
}

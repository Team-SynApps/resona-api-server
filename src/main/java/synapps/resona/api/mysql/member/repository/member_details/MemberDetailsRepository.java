package synapps.resona.api.mysql.member.repository.member_details;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;

@Repository
public interface MemberDetailsRepository extends JpaRepository<MemberDetails, Long> {

}

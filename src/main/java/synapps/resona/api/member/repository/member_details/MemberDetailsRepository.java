package synapps.resona.api.member.repository.member_details;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member_details.MemberDetails;

@MySQLRepository
public interface MemberDetailsRepository extends JpaRepository<MemberDetails, Long> {

}

package synapps.resona.api.mysql.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.notification.MemberPushToken;

@Repository
public interface MemberPushTokenRepository extends
    JpaRepository<MemberPushToken, Long> {

}

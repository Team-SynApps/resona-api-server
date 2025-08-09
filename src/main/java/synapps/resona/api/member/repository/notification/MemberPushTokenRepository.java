package synapps.resona.api.member.repository.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.notification.MemberPushToken;

@MySQLRepository
public interface MemberPushTokenRepository extends
    JpaRepository<MemberPushToken, Long> {

}

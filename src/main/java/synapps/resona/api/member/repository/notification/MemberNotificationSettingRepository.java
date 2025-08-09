package synapps.resona.api.member.repository.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.entity.notification.MemberNotificationSetting;

import java.util.Optional;

@MySQLRepository
public interface MemberNotificationSettingRepository extends
    JpaRepository<MemberNotificationSetting, Long> {

    Optional<MemberNotificationSetting> findByMember(Member member);
}

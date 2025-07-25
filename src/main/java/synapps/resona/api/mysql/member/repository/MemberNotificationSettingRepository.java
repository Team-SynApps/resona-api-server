package synapps.resona.api.mysql.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.notification.MemberNotificationSetting;

import java.util.Optional;

@Repository
public interface MemberNotificationSettingRepository extends
    JpaRepository<MemberNotificationSetting, Long> {

    Optional<MemberNotificationSetting> findByMember(Member member);
}

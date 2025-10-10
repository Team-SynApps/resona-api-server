package com.synapps.resona.repository;

import com.synapps.resona.entity.MemberNotificationSetting;
import com.synapps.resona.entity.NotificationMember;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface MemberNotificationSettingRepository extends
    JpaRepository<MemberNotificationSetting, Long> {

    Optional<MemberNotificationSetting> findByMember(NotificationMember member);
}

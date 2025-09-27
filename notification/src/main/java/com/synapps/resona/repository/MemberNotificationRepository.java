package com.synapps.resona.repository;

import com.synapps.resona.entity.MemberNotification;
import com.synapps.resona.entity.NotificationMember;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface MemberNotificationRepository extends JpaRepository<MemberNotification, Long> {

    List<MemberNotification> findByMemberAndIdLessThanOrderByIdDesc(NotificationMember member, Long id, Pageable pageable);
}

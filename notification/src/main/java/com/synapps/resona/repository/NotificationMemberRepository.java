package com.synapps.resona.repository;

import com.synapps.resona.entity.NotificationMember;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface NotificationMemberRepository extends JpaRepository<NotificationMember, Long> {
}

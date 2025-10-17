package com.synapps.resona.command.repository.account;

import com.synapps.resona.command.entity.account.AccountInfo;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface AccountInfoRepository extends JpaRepository<AccountInfo, Long> {
//    AccountInfo findByMember(Member member);

//    @Query("SELECT a FROM AccountInfo a WHERE a.status = :status AND a.modifiedAt < :dateTime")
//    List<AccountInfo> findExpiredTemporaryAccounts(@Param("status") AccountStatus status, @Param("dateTime") LocalDateTime dateTime);
}

package synapps.resona.api.member.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.account.AccountInfo;

@MySQLRepository
public interface AccountInfoRepository extends JpaRepository<AccountInfo, Long> {
//    AccountInfo findByMember(Member member);

//    @Query("SELECT a FROM AccountInfo a WHERE a.status = :status AND a.modifiedAt < :dateTime")
//    List<AccountInfo> findExpiredTemporaryAccounts(@Param("status") AccountStatus status, @Param("dateTime") LocalDateTime dateTime);
}

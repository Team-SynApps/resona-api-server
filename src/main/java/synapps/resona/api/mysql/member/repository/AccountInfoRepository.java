package synapps.resona.api.mysql.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.entity.member.Member;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccountInfoRepository extends JpaRepository<AccountInfo, Long> {
    AccountInfo findByMember(Member member);

    @Query("SELECT a FROM AccountInfo a WHERE a.status = :status AND a.modifiedAt < :dateTime")
    List<AccountInfo> findExpiredTemporaryAccounts(@Param("status") AccountStatus status, @Param("dateTime") LocalDateTime dateTime);
}

package synapps.resona.api.mysql.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;

public interface AccountInfoRepository extends JpaRepository<AccountInfo, Long> {
    AccountInfo findByMemberId(long memberId);
}

package synapps.resona.api.member.repository.notification;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.notification.entity.MemberPushToken;

@MySQLRepository
public interface MemberPushTokenRepository extends
    JpaRepository<MemberPushToken, Long> {

  @Query("SELECT mpt "
      + "FROM MemberPushToken mpt "
      + "WHERE mpt.member.id = :memberId AND mpt.isActive = true")
  List<MemberPushToken> findActiveTokensByMemberId(@Param("memberId") Long memberId);
}

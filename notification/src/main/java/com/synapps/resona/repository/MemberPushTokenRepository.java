package com.synapps.resona.repository;

import com.synapps.resona.entity.MemberPushToken;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@MySQLRepository
public interface MemberPushTokenRepository extends
    JpaRepository<MemberPushToken, Long> {

  @Query("SELECT mpt "
      + "FROM MemberPushToken mpt "
      + "WHERE mpt.member.id = :memberId AND mpt.isActive = true")
  List<MemberPushToken> findActiveTokensByMemberId(@Param("memberId") Long memberId);
}

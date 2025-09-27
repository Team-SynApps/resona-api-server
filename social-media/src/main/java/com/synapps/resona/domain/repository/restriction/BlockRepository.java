package com.synapps.resona.domain.repository.restriction;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.domain.entity.restriction.Block;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@MySQLRepository
public interface BlockRepository extends JpaRepository<Block, Long> , BlockRepositoryCustom {

  boolean existsByBlockerAndBlocked(Member blocker, Member blocked);

  @Query(value = "SELECT * FROM block WHERE blocker_id = :blockerId AND blocked_id = :blockedId", nativeQuery = true)
  Optional<Block> findByBlockerAndBlockedIncludeDeleted(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);
}

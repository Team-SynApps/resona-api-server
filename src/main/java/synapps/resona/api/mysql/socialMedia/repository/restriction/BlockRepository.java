package synapps.resona.api.mysql.socialMedia.repository.restriction;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.socialMedia.entity.restriction.Block;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> , BlockRepositoryCustom {

  boolean existsByBlockerAndBlocked(Member blocker, Member blocked);

  @Query(value = "SELECT * FROM block WHERE blocker_id = :blockerId AND blocked_id = :blockedId", nativeQuery = true)
  Optional<Block> findByBlockerAndBlockedIncludeDeleted(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);
}

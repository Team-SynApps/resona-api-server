package synapps.resona.api.socialMedia.repository.restriction;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.entity.restriction.Block;

@MySQLRepository
public interface BlockRepository extends JpaRepository<Block, Long> , BlockRepositoryCustom {

  boolean existsByBlockerAndBlocked(Member blocker, Member blocked);

  @Query(value = "SELECT * FROM block WHERE blocker_id = :blockerId AND blocked_id = :blockedId", nativeQuery = true)
  Optional<Block> findByBlockerAndBlockedIncludeDeleted(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);
}

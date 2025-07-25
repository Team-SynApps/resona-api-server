package synapps.resona.api.mysql.socialMedia.repository.feed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.socialMedia.entity.feed.Scrap;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

  boolean existsByIdAndMember(Long scrapId, Member member);

  List<Scrap> findAllByMember(Member member);

  boolean existsByMemberAndFeedId(Member member, Long feedId);

  @Query("SELECT COUNT(s) FROM Scrap s WHERE s.feed.id = :feedId")
  Long countByFeedId(@Param("feedId") Long feedId);

  @Query("SELECT s FROM Scrap s " +
      "JOIN FETCH s.feed f " +
      "WHERE s.member.id = :memberId AND s.createdAt < :cursor " +
      "ORDER BY s.createdAt DESC")
  List<Scrap> findScrapsByCursorAndMemberId(@Param("memberId") Long memberId,
      @Param("cursor") LocalDateTime cursor,
      Pageable pageable);

  Optional<Scrap> findByMemberAndFeedId(Member member, Long feedId);
}

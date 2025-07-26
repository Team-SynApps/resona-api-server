package synapps.resona.api.mysql.socialMedia.repository.feed;

import jakarta.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {

  // for test
  List<Feed> findAllByMember(Member member);

  @Query("SELECT DISTINCT f FROM Feed f " +
      "LEFT JOIN FETCH f.images " +
      "WHERE f.member.id = :memberId")
  List<Feed> findFeedsWithImagesByMemberId(@Param("memberId") Long memberId);

  @Query("SELECT DISTINCT f FROM Feed f " +
      "LEFT JOIN FETCH f.images " +
      "WHERE f.id = :feedId")
  Optional<Feed> findFeedWithImagesByFeedId(@Param("feedId") Long feedId);

  @Query("SELECT f.id, COUNT(l) FROM Feed f " +
      "LEFT JOIN f.likes l " +
      "WHERE f.member.id = :memberId " +
      "GROUP BY f.id")
  List<Object[]> countLikesByMemberId(@Param("memberId") Long memberId);

  // cursor
//    @Query("SELECT f FROM Feed f WHERE f.createdAt < :cursor " +
//            "ORDER BY f.createdAt DESC LIMIT :size")
//    List<Feed> findFeedsByCursor(LocalDateTime cursor, int size);

  @Query("SELECT f FROM Feed f " +
      "JOIN FETCH f.member " +
      "WHERE f.id = :feedId ")
  Optional<Feed> findWithMemberById(@Param("feedId") Long feedId);

  @Query("SELECT DISTINCT f FROM Feed f " +
      "LEFT JOIN FETCH f.comments " +
      "WHERE f.id = :feedId")
  Optional<Feed> findWithCommentById(@Param("feedId") Long feedId);

  @QueryHints(value = @QueryHint(name = "org.hibernate.hint.USE_INDEX", value = "idx_created_at"))
  @Query("SELECT f FROM Feed f " +
      "WHERE f.createdAt < :cursor " +
      "ORDER BY f.createdAt DESC")
  List<Feed> findFeedsByCursor(@Param("cursor") LocalDateTime cursor, Pageable pageable);


  @Query("SELECT f FROM Feed f " +
      "JOIN FETCH f.member " +
      "WHERE f.member.id = :memberId AND f.createdAt < :cursor " +
      "ORDER BY f.createdAt DESC")
  List<Feed> findFeedsByCursorAndMemberId(@Param("memberId") Long memberId,
      @Param("cursor") LocalDateTime cursor, Pageable pageable);


  boolean existsByIdAndMember(Long feedId, Member member);
}

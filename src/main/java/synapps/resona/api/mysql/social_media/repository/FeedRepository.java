package synapps.resona.api.mysql.social_media.repository;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.social_media.entity.Comment;
import synapps.resona.api.mysql.social_media.entity.Feed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {
    // for test
    List<Feed> findAllByMember(Member member);

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
    @Query("SELECT  f FROM Feed f WHERE f.createdAt < :cursor " +
            "ORDER BY f.createdAt DESC LIMIT :size")
    List<Feed> findFeedsByCursor(LocalDateTime cursor, int size);


    boolean existsByIdAndMember(Long feedId, Member member);
}

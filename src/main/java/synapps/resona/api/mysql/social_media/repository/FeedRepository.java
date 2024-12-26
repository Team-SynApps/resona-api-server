package synapps.resona.api.mysql.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.social_media.entity.Feed;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    // for test
    List<Feed> findAllByMember(Member member);

    // cursor
    @Query("SELECT f FROM Feed f WHERE f.createdAt < :cursor " +
            "ORDER BY f.createdAt DESC LIMIT :size")
    List<Feed> findFeedsByCursor(LocalDateTime cursor, int size);
    //    @QueryHints(value = @QueryHint(name = "org.hibernate.hint.USE_INDEX", value = "idx_created_at"))
//    @Query("SELECT  f FROM Feed f WHERE f.createdAt < :cursor " +
//            "ORDER BY f.createdAt DESC LIMIT :size")
//    List<Feed> findFeedsByCursor(LocalDateTime cursor, int size);
}

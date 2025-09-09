package synapps.resona.api.socialMedia.repository.feed;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.entity.feed.Likes;

@MySQLRepository
public interface LikesRepository extends JpaRepository<Likes, Long> {

  boolean existsByIdAndMember(Long likeId, Member member);

  @Query("SELECT l FROM Likes l "
      + "WHERE l.feed.id = :feedId AND l.member.id = :memberId ")
  Optional<Likes> findLikesByFeedId(@Param("feedId") Long feedId, @Param("memberId") Long memberId);
}
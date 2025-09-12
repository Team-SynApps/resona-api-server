package synapps.resona.api.socialMedia.repository.likes;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.entity.feed.Feed;
import synapps.resona.api.socialMedia.entity.likes.FeedLikes;

@MySQLRepository
public interface FeedLikesRepository extends JpaRepository<FeedLikes, Long> {

  boolean existsByFeedAndMember(Feed feed, Member member);

  Optional<FeedLikes> findByFeedAndMember(Feed feed, Member member);

  long countByFeedId(Long feedId);
}
package synapps.resona.api.mysql.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.social_media.entity.Feed;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    // for test
    List<Feed> findAllByMember(Member member);
}

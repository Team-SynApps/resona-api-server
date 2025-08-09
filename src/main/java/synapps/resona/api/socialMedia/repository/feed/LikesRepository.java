package synapps.resona.api.socialMedia.repository.feed;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.entity.feed.Likes;

@MySQLRepository
public interface LikesRepository extends JpaRepository<Likes, Long> {

  boolean existsByIdAndMember(Long likeId, Member member);
}

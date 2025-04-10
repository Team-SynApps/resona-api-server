package synapps.resona.api.mysql.socialMedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.socialMedia.entity.Likes;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {
    boolean existsByIdAndMember(Long likeId, Member member);
}

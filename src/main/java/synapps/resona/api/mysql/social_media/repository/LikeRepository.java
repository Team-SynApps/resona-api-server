package synapps.resona.api.mysql.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.mysql.social_media.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
}

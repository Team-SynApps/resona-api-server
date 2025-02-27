package synapps.resona.api.mysql.social_media.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.social_media.entity.Mention;

public interface MentionRepository extends JpaRepository<Mention, Long> {
    boolean existsByIdAndMember(Long mentionId, Member member);
}

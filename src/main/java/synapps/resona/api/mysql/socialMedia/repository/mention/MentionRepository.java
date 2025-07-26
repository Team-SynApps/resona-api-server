package synapps.resona.api.mysql.socialMedia.repository.mention;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.socialMedia.entity.mention.Mention;

@Repository
public interface MentionRepository extends JpaRepository<Mention, Long> {

  boolean existsByIdAndMember(Long mentionId, Member member);
}

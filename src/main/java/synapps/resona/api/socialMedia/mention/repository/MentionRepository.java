package synapps.resona.api.socialMedia.mention.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.mention.entity.Mention;

@MySQLRepository
public interface MentionRepository extends JpaRepository<Mention, Long> {

  boolean existsByIdAndMember(Long mentionId, Member member);
}

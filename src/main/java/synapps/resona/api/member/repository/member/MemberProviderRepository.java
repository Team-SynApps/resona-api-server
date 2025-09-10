package synapps.resona.api.member.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.member.entity.member.MemberProvider;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.oauth.entity.ProviderType;
import java.util.Optional;

@MySQLRepository
public interface MemberProviderRepository extends JpaRepository<MemberProvider, Long> {
  Optional<MemberProvider> findByMemberAndProviderType(Member member, ProviderType providerType);
}
package synapps.resona.api.mysql.member.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.mysql.member.entity.member.MemberProvider;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.oauth.entity.ProviderType;
import java.util.Optional;

public interface MemberProviderRepository extends JpaRepository<MemberProvider, Long> {
  Optional<MemberProvider> findByMemberAndProviderType(Member member, ProviderType providerType);
}
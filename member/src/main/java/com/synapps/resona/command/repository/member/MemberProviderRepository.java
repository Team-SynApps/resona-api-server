package com.synapps.resona.command.repository.member;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.entity.member.MemberProvider;
import com.synapps.resona.command.entity.account.ProviderType;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface MemberProviderRepository extends JpaRepository<MemberProvider, Long> {
  Optional<MemberProvider> findByMemberAndProviderType(Member member, ProviderType providerType);
}
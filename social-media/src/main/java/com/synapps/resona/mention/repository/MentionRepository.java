package com.synapps.resona.mention.repository;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.mention.entity.Mention;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface MentionRepository extends JpaRepository<Mention, Long> {

  boolean existsByIdAndMember(Long mentionId, Member member);
}

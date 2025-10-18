package com.synapps.resona.query.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MongoDBRepository;
import com.synapps.resona.query.entity.MemberStateDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

@MongoDBRepository
public interface MemberStateRepository extends MongoRepository<MemberStateDocument, Long> {
  Optional<MemberStateDocument> findByMemberId(Long memberId);
}
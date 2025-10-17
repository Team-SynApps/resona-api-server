package com.synapps.resona.query.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MongoDBRepository;
import com.synapps.resona.query.entity.MemberStateDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

@MongoDBRepository
public interface MemberStateDocumentRepository extends MongoRepository<MemberStateDocument, Long> {
}

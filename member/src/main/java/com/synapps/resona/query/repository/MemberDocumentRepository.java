package com.synapps.resona.query.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MongoDBRepository;
import com.synapps.resona.query.entity.MemberDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

@MongoDBRepository
public interface MemberDocumentRepository extends MongoRepository<MemberDocument, Long> {

    Optional<MemberDocument> findByEmail(String email);

    boolean existsByProfileTag(String tag);
}

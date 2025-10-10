package com.synapps.resona.comment.query.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MongoDBRepository;
import com.synapps.resona.comment.query.entity.CommentDocument;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

@MongoDBRepository
public interface CommentDocumentRepository extends MongoRepository<CommentDocument, ObjectId> {
  Optional<CommentDocument> findByCommentId(Long commentId);
  Page<CommentDocument> findByFeedIdOrderByCreatedAtDesc(Long feedId, Pageable pageable);
}

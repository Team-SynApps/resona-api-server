package com.synapps.resona.retrieval.query.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MongoDBRepository;
import com.synapps.resona.entity.profile.CountryCode;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

@MongoDBRepository
public interface FeedReadRepository extends MongoRepository<FeedDocument, ObjectId> {

  Optional<FeedDocument> findByFeedId(Long feedId);

  List<FeedDocument> findAllByFeedIdIn(Set<Long> feedIds);

  List<FeedDocument> findAllByOrderByCreatedAtDesc(Pageable pageable);

  Page<FeedDocument> findByAuthor_MemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

  List<FeedDocument> findByCategoryOrderByCreatedAtDesc(FeedCategory category, Pageable pageable);

  Page<FeedDocument> findByFeedIdInOrderByCreatedAtDesc(Set<Long> feedIds, Pageable pageable);

  @Query("{ 'author.countryOfResidence' : ?0 }")
  List<FeedDocument> findByCountry(CountryCode countryOfResidence, Pageable pageable);

  @Query("{ 'author.countryOfResidence' : ?0, 'category' : ?1 }")
  List<FeedDocument> findByCountryAndCategory(CountryCode countryOfResidence, FeedCategory category, Pageable pageable);
}
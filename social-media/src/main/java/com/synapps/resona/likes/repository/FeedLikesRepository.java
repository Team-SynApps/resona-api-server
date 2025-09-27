package com.synapps.resona.likes.repository;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.entity.Feed;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.likes.entity.FeedLikes;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface FeedLikesRepository extends JpaRepository<FeedLikes, Long> {

  boolean existsByFeedAndMember(Feed feed, Member member);

  Optional<FeedLikes> findByFeedAndMember(Feed feed, Member member);

  long countByFeedId(Long feedId);
}
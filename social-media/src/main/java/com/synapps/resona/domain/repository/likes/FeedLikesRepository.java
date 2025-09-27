package com.synapps.resona.domain.repository.likes;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.domain.entity.feed.Feed;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.domain.entity.likes.FeedLikes;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface FeedLikesRepository extends JpaRepository<FeedLikes, Long> {

  boolean existsByFeedAndMember(Feed feed, Member member);

  Optional<FeedLikes> findByFeedAndMember(Feed feed, Member member);

  long countByFeedId(Long feedId);
}
package com.synapps.resona.feed.command.repository;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.feed.command.entity.FeedLikes;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface FeedLikesRepository extends JpaRepository<FeedLikes, Long> {

  Optional<FeedLikes> findByMemberAndFeed(Member member, Feed feed);

  boolean existsByFeedAndMember(Feed feed, Member member);
}
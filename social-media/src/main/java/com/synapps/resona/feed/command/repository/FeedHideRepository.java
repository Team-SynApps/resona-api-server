package com.synapps.resona.feed.command.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.entity.FeedHide;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface FeedHideRepository extends JpaRepository<FeedHide, Long> {
  Optional<FeedHide> findByMemberAndFeed(Member member, Feed feed);
}
package com.synapps.resona.feed.command.repository;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.entity.Scrap;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
  Optional<Scrap> findByMemberAndFeed(Member member, Feed feed);
}

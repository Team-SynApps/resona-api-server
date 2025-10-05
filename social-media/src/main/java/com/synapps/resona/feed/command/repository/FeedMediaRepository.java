package com.synapps.resona.feed.command.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.feed.command.entity.FeedMedia;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface FeedMediaRepository extends JpaRepository<FeedMedia, Long> {

}

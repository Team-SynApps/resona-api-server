package com.synapps.resona.media.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.media.entity.FeedMedia;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface FeedMediaRepository extends JpaRepository<FeedMedia, Long> {

}

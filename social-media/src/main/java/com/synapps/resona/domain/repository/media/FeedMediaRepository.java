package com.synapps.resona.domain.repository.media;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.domain.entity.media.FeedMedia;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface FeedMediaRepository extends JpaRepository<FeedMedia, Long> {

}

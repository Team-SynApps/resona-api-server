package com.synapps.resona.domain.repository.feed;

import com.synapps.resona.domain.entity.feed.Location;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface LocationRepository extends JpaRepository<Location, Long> {

}

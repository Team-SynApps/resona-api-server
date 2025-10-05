package com.synapps.resona.feed.command.repository;

import com.synapps.resona.feed.command.entity.Location;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface LocationRepository extends JpaRepository<Location, Long> {

}

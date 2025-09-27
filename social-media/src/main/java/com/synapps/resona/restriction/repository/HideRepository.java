package com.synapps.resona.restriction.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.restriction.entity.Hide;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface HideRepository extends JpaRepository<Hide, Long> {

}

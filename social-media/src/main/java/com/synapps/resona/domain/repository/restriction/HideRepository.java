package com.synapps.resona.domain.repository.restriction;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.domain.entity.restriction.Hide;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface HideRepository extends JpaRepository<Hide, Long> {

}

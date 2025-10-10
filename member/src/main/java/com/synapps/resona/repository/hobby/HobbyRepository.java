package com.synapps.resona.repository.hobby;

import com.synapps.resona.entity.hobby.Hobby;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface HobbyRepository extends JpaRepository<Hobby, Long> {

  Optional<Hobby> findByMemberDetailsIdAndName(Long memberDetailsId, String name);
}

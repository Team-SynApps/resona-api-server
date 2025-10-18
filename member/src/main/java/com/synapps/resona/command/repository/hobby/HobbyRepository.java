package com.synapps.resona.command.repository.hobby;

import com.synapps.resona.command.entity.hobby.Hobby;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface HobbyRepository extends JpaRepository<Hobby, Long> {

  Optional<Hobby> findByMemberDetailsIdAndName(Long memberDetailsId, String name);

  List<Hobby> findAllByMemberDetailsId(Long memberDetailsId);
}

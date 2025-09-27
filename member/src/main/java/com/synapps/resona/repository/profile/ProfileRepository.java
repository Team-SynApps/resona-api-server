package com.synapps.resona.repository.profile;

import com.synapps.resona.entity.profile.Profile;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

  boolean existsByTag(String tag);
}

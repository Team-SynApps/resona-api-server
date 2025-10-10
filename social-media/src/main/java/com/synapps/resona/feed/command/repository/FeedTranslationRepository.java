package com.synapps.resona.feed.command.repository;

import com.oracle.bmc.identitydomains.model.Operations.Op;
import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.entity.Language;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.entity.FeedTranslation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface FeedTranslationRepository extends JpaRepository<FeedTranslation, Long> {

  Optional<FeedTranslation> findByFeedAndLanguage(Feed feed, Language language);
}

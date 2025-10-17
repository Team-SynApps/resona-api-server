package com.synapps.resona.retrieval.service.strategy;

import com.synapps.resona.command.entity.profile.CountryCode;
import com.synapps.resona.feed.command.entity.FeedCategory;
import org.springframework.data.domain.Pageable;

public record FallbackStrategyContext(
    Pageable pageable,
    FeedCategory category,
    CountryCode countryOfResidence
) {
}
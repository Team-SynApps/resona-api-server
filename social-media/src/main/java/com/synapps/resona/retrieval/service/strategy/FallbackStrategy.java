package com.synapps.resona.retrieval.service.strategy;

import com.synapps.resona.retrieval.query.entity.FeedDocument;
import java.util.List;

public interface FallbackStrategy {

  boolean supports(FallbackStrategyContext context);

  List<FeedDocument> findFeeds(FallbackStrategyContext context);
}
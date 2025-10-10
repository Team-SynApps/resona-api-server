package com.synapps.resona.retrieval.port.in;

import com.synapps.resona.feed.event.FeedCreatedEvent;
import com.synapps.resona.feed.event.FeedLikeChangedEvent;
import com.synapps.resona.feed.event.FeedUpdatedEvent;

public interface FeedReadModelSyncUseCase {

  void syncCreatedFeed(FeedCreatedEvent event);

  void syncUpdatedFeed(FeedUpdatedEvent event);

  void syncLikedFeed(FeedLikeChangedEvent event);
}

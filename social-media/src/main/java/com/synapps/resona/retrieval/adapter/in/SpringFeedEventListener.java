package com.synapps.resona.retrieval.adapter.in;

import com.synapps.resona.feed.event.FeedCreatedEvent;
import com.synapps.resona.feed.event.FeedUpdatedEvent;
import com.synapps.resona.retrieval.port.in.FeedReadModelSyncUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SpringFeedEventListener {

  private final FeedReadModelSyncUseCase feedReadModelSyncUseCase;

  @TransactionalEventListener
  public void handleFeedCreatedEvent(FeedCreatedEvent event) {
    feedReadModelSyncUseCase.syncCreatedFeed(event);
  }

  @TransactionalEventListener
  public void handleFeedUpdatedEvent(FeedUpdatedEvent event) {
    feedReadModelSyncUseCase.syncUpdatedFeed(event);
  }
}
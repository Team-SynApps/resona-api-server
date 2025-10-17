package com.synapps.resona.retrieval.adapter.in;

import com.synapps.resona.feed.event.FeedCreatedEvent;
import com.synapps.resona.feed.event.FeedLikeChangedEvent;
import com.synapps.resona.feed.event.FeedUpdatedEvent;
import com.synapps.resona.retrieval.port.in.FeedReadModelSyncUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
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

  @Async
  @TransactionalEventListener
  public void handleFeedLikeChanged(FeedLikeChangedEvent event) {
    feedReadModelSyncUseCase.syncLikedFeed(event);
  }
}
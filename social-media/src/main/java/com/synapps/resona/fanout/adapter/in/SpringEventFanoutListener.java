package com.synapps.resona.fanout.adapter.in;

import com.synapps.resona.fanout.port.in.FanoutUseCase;
import com.synapps.resona.feed.event.FeedCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SpringEventFanoutListener {
  private final FanoutUseCase fanoutUseCase;

  @TransactionalEventListener
  public void handleFeedCreatedEvent(FeedCreatedEvent event) {
    fanoutUseCase.fanoutFeed(event);
  }
}

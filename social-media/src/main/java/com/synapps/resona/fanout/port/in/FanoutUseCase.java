package com.synapps.resona.fanout.port.in;

import com.synapps.resona.feed.event.FeedCreatedEvent;

public interface FanoutUseCase {
  void fanoutFeed(FeedCreatedEvent event);
}
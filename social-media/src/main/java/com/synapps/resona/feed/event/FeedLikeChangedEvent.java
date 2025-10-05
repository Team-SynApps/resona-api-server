package com.synapps.resona.feed.event;

public record FeedLikeChangedEvent(
    Long memberId,
    Long feedId,
    int delta
) {
}
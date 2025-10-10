package com.synapps.resona.feed.event;

public record FeedScrappedEvent(
    Long memberId,
    Long feedId,
    boolean isScrapped
) {
}
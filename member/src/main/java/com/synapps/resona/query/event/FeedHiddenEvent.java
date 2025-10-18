package com.synapps.resona.query.event;

public record FeedHiddenEvent(
    Long memberId,
    Long feedId
) {
}
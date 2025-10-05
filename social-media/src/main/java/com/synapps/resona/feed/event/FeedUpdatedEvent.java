package com.synapps.resona.feed.event;

import java.time.LocalDateTime;

public record FeedUpdatedEvent(
    Long feedId,
    String content,
    LocalDateTime updatedAt
) {
}

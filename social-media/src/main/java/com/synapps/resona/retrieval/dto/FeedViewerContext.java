package com.synapps.resona.retrieval.dto;

import java.util.Set;

public record FeedViewerContext(
    Long viewerId,
    Set<Long> hiddenFeedIds,
    Set<Long> blockedMemberIds,
    Set<Long> likedFeedIds,
    Set<Long> scrappedFeedIds
) {
}
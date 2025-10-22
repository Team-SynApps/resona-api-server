package com.synapps.resona.retrieval.service;

import com.synapps.resona.retrieval.dto.FeedViewerContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedViewerContextFactory {
  private final FeedQueryHelper feedQueryHelper;

  public FeedViewerContext create(Long viewerId) {
    return new FeedViewerContext(
        viewerId,
        feedQueryHelper.getHiddenFeedIds(viewerId),
        feedQueryHelper.getBlockedMemberIds(viewerId),
        feedQueryHelper.getLikedFeedIds(viewerId),
        feedQueryHelper.getScrappedFeedIds(viewerId)
    );
  }
}

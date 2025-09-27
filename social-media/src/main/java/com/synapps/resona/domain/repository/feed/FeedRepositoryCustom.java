package com.synapps.resona.domain.repository.feed;


import com.synapps.resona.query.dto.feed.FeedDetailDto;

public interface FeedRepositoryCustom {

  FeedDetailDto findFeedDetailById(Long feedId, Long viewerId);
}

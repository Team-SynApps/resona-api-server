package com.synapps.resona.feed.repository;


import com.synapps.resona.feed.dto.FeedDetailDto;

public interface FeedRepositoryCustom {

  FeedDetailDto findFeedDetailById(Long feedId, Long viewerId);
}

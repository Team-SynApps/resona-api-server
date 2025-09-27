package com.synapps.resona.media.service;

import com.synapps.resona.feed.entity.Feed;
import com.synapps.resona.feed.exception.FeedException;
import com.synapps.resona.feed.exception.FeedMediaException;
import com.synapps.resona.feed.repository.FeedRepository;
import com.synapps.resona.media.dto.request.FeedImageRequest;
import com.synapps.resona.media.entity.FeedMedia;
import com.synapps.resona.media.repository.FeedMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedMediaService {

  private final FeedRepository feedRepository;
  private final FeedMediaRepository feedMediaRepository;

  @Transactional
  public FeedMedia register(FeedImageRequest feedImageRequest) {
    Feed feed = feedRepository.findById(feedImageRequest.getFeedId())
        .orElseThrow(FeedException::feedNotFoundException);
    FeedMedia feedMedia = FeedMedia.of(feed, feedImageRequest.getUrl(), feedImageRequest.getType(),
        feedImageRequest.getIndex());
    feedMediaRepository.save(feedMedia);
    return feedMedia;
  }

  @Transactional
  public FeedMedia update(FeedImageRequest feedImageRequest) {
    FeedMedia feedMedia = feedMediaRepository.findById(feedImageRequest.getFeedId())
        .orElseThrow(FeedMediaException::imageNotFound);
    feedMedia.updateUrl(feedImageRequest.getUrl());
    return feedMedia;
  }

  public FeedMedia readFeedImage(Long feedImageId) {
    return feedMediaRepository.findById(feedImageId).orElseThrow(FeedMediaException::imageNotFound);
  }

  @Transactional
  public FeedMedia deleteFeedImage(Long feedImageId) {
    FeedMedia feedMedia = feedMediaRepository.findById(feedImageId)
        .orElseThrow(FeedMediaException::imageNotFound);
    feedMedia.softDelete();
    return feedMedia;
  }
}

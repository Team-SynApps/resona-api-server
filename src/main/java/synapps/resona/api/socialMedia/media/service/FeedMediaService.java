package synapps.resona.api.socialMedia.media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.socialMedia.media.dto.request.FeedImageRequest;
import synapps.resona.api.socialMedia.feed.entity.Feed;
import synapps.resona.api.socialMedia.media.entity.FeedMedia;
import synapps.resona.api.socialMedia.feed.exception.FeedException;
import synapps.resona.api.socialMedia.feed.exception.FeedMediaException;
import synapps.resona.api.socialMedia.media.repository.FeedMediaRepository;
import synapps.resona.api.socialMedia.feed.repository.FeedRepository;

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

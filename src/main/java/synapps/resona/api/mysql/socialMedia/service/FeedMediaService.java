package synapps.resona.api.mysql.socialMedia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.socialMedia.dto.feedImage.request.FeedImageRequest;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.entity.feedMedia.FeedMedia;
import synapps.resona.api.mysql.socialMedia.exception.FeedException;
import synapps.resona.api.mysql.socialMedia.exception.FeedMediaException;
import synapps.resona.api.mysql.socialMedia.repository.FeedMediaRepository;
import synapps.resona.api.mysql.socialMedia.repository.FeedRepository;

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

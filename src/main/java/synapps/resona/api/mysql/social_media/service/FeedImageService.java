package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.social_media.dto.feed.FeedImageRequest;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.entity.FeedMedia;
import synapps.resona.api.mysql.social_media.exception.FeedImageNotFoundException;
import synapps.resona.api.mysql.social_media.exception.FeedNotFoundException;
import synapps.resona.api.mysql.social_media.repository.FeedImageRepository;
import synapps.resona.api.mysql.social_media.repository.FeedRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedImageService {
    private final FeedRepository feedRepository;
    private final FeedImageRepository feedImageRepository;

    @Transactional
    public FeedMedia register(FeedImageRequest feedImageRequest) throws FeedNotFoundException {
        Feed feed = feedRepository.findById(feedImageRequest.getFeedId()).orElseThrow(FeedNotFoundException::new);
        FeedMedia feedMedia = FeedMedia.of(feed, feedImageRequest.getUrl(), LocalDateTime.now(), LocalDateTime.now());
        feedImageRepository.save(feedMedia);
        return feedMedia;
    }

    @Transactional
    public FeedMedia update(FeedImageRequest feedImageRequest) throws FeedImageNotFoundException {
        FeedMedia feedMedia = feedImageRepository.findById(feedImageRequest.getFeedId()).orElseThrow(FeedImageNotFoundException::new);
        feedMedia.updateUrl(feedImageRequest.getUrl());
        return feedMedia;
    }

    public FeedMedia readFeedImage(Long feedImageId) throws FeedImageNotFoundException {
        return feedImageRepository.findById(feedImageId).orElseThrow(FeedImageNotFoundException::new);
    }

    @Transactional
    public FeedMedia deleteFeedImage(Long feedImageId) throws FeedImageNotFoundException {
        FeedMedia feedMedia = feedImageRepository.findById(feedImageId).orElseThrow(FeedImageNotFoundException::new);
        feedMedia.softDelete();
        return feedMedia;
    }
}

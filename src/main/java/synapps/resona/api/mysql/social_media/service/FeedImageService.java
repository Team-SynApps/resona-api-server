package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.social_media.dto.FeedImageRequest;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.entity.FeedImage;
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
    public FeedImage register(FeedImageRequest feedImageRequest) throws FeedNotFoundException {
        Feed feed = feedRepository.findById(feedImageRequest.getFeedId()).orElseThrow(FeedNotFoundException::new);
        FeedImage feedImage = FeedImage.of(feed, feedImageRequest.getUrl(), LocalDateTime.now(), LocalDateTime.now());
        feedImageRepository.save(feedImage);
        return feedImage;
    }

    @Transactional
    public FeedImage update(FeedImageRequest feedImageRequest) throws FeedImageNotFoundException {
        FeedImage feedImage = feedImageRepository.findById(feedImageRequest.getFeedId()).orElseThrow(FeedImageNotFoundException::new);
        feedImage.updateUrl(feedImageRequest.getUrl());
        return feedImage;
    }

    public FeedImage readFeedImage(Long feedImageId) throws FeedImageNotFoundException {
        return feedImageRepository.findById(feedImageId).orElseThrow(FeedImageNotFoundException::new);
    }

    @Transactional
    public FeedImage deleteFeedImage(Long feedImageId) throws FeedImageNotFoundException {
        FeedImage feedImage = feedImageRepository.findById(feedImageId).orElseThrow(FeedImageNotFoundException::new);
        feedImage.softDelete();
        return feedImage;
    }
}

package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.social_media.dto.feed.request.FeedImageRequest;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.entity.FeedMedia;
import synapps.resona.api.mysql.social_media.exception.FeedException;
import synapps.resona.api.mysql.social_media.exception.FeedImageNotFoundException;
import synapps.resona.api.mysql.social_media.repository.FeedMediaRepository;
import synapps.resona.api.mysql.social_media.repository.FeedRepository;

@Service
@RequiredArgsConstructor
public class FeedMediaService {
    private final FeedRepository feedRepository;
    private final FeedMediaRepository feedMediaRepository;

    @Transactional
    public FeedMedia register(FeedImageRequest feedImageRequest){
        Feed feed = feedRepository.findById(feedImageRequest.getFeedId()).orElseThrow(FeedException::feedNotFoundException);
        FeedMedia feedMedia = FeedMedia.of(feed, feedImageRequest.getUrl(), feedImageRequest.getType(), feedImageRequest.getIndex());
        feedMediaRepository.save(feedMedia);
        return feedMedia;
    }

    @Transactional
    public FeedMedia update(FeedImageRequest feedImageRequest) throws FeedImageNotFoundException {
        FeedMedia feedMedia = feedMediaRepository.findById(feedImageRequest.getFeedId()).orElseThrow(FeedImageNotFoundException::new);
        feedMedia.updateUrl(feedImageRequest.getUrl());
        return feedMedia;
    }

    public FeedMedia readFeedImage(Long feedImageId) throws FeedImageNotFoundException {
        return feedMediaRepository.findById(feedImageId).orElseThrow(FeedImageNotFoundException::new);
    }

    @Transactional
    public FeedMedia deleteFeedImage(Long feedImageId) throws FeedImageNotFoundException {
        FeedMedia feedMedia = feedMediaRepository.findById(feedImageId).orElseThrow(FeedImageNotFoundException::new);
        feedMedia.softDelete();
        return feedMedia;
    }
}

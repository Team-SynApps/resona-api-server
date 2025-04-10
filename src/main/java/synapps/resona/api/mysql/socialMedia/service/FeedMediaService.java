package synapps.resona.api.mysql.socialMedia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedImageRequest;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.entity.feedMedia.FeedMedia;
import synapps.resona.api.mysql.socialMedia.exception.FeedException;
import synapps.resona.api.mysql.socialMedia.exception.FeedImageNotFoundException;
import synapps.resona.api.mysql.socialMedia.repository.FeedMediaRepository;
import synapps.resona.api.mysql.socialMedia.repository.FeedRepository;

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

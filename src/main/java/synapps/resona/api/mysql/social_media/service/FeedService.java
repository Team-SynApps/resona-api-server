package synapps.resona.api.mysql.social_media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.social_media.dto.feed.FeedRequest;
import synapps.resona.api.mysql.social_media.dto.feed.FeedUpdateRequest;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.exception.FeedNotFoundException;
import synapps.resona.api.mysql.social_media.repository.FeedRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final MemberService memberService;

    @Transactional
    public Feed register(FeedRequest feedRequest) {
        Member member = memberService.getMember();
        Feed feed = Feed.of(member, feedRequest.getContent(), LocalDateTime.now(), LocalDateTime.now());
        return feedRepository.save(feed);
    }

    @Transactional
    public Feed updateFeed(FeedUpdateRequest feedRequest) throws FeedNotFoundException {
        // 예외처리 해줘야 함
        Feed feed = feedRepository.findById(feedRequest.getFeedId()).orElseThrow(FeedNotFoundException::new);
        feed.updateContent(feedRequest.getContent());
        return feed;
    }

    public Feed readFeed(Long feedId) throws FeedNotFoundException {
        return feedRepository.findById(feedId).orElseThrow(FeedNotFoundException::new);
    }

    @Transactional
    public Feed deleteFeed(Long feedId) throws FeedNotFoundException {
        Feed feed  = feedRepository.findById(feedId).orElseThrow(FeedNotFoundException::new);
        feed.softDelete();
        return feed;
    }
}

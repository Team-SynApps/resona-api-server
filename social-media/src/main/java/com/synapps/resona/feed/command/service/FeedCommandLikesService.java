package com.synapps.resona.feed.command.service;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.entity.FeedLikes;
import com.synapps.resona.feed.command.repository.FeedLikesRepository;
import com.synapps.resona.feed.event.FeedLikeChangedEvent;
import com.synapps.resona.command.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedCommandLikesService {

  private final FeedLikesRepository feedLikesRepository;
  private final MemberService memberService;
  private final FeedCommandService feedCommandService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void likeFeed(Long memberId, Long feedId) {
    Member member = memberService.getMember(memberId);
    Feed feed = feedCommandService.getFeed(feedId);

    if (feedLikesRepository.findByMemberAndFeed(member, feed).isPresent()) {
      return;
    }

    feedLikesRepository.save(FeedLikes.of(member, feed));
    feed.increaseLikeCount();

    eventPublisher.publishEvent(new FeedLikeChangedEvent(memberId, feedId, 1));
  }

  @Transactional
  public void unlikeFeed(Long memberId, Long feedId) {
    Member member = memberService.getMember(memberId);
    Feed feed = feedCommandService.getFeed(feedId);

    feedLikesRepository.findByMemberAndFeed(member, feed).ifPresent(feedLike -> {
      feedLike.softDelete();
      feed.decreaseLikeCount();
      eventPublisher.publishEvent(new FeedLikeChangedEvent(memberId, feedId, -1));
    });
  }
}
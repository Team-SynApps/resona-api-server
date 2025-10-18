package com.synapps.resona.feed.command.service;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.entity.FeedHide;
import com.synapps.resona.feed.command.repository.FeedHideRepository;
import com.synapps.resona.query.event.FeedHiddenEvent;
import com.synapps.resona.command.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCommandHideService {
  private final FeedHideRepository feedHideRepository;
  private final FeedCommandService feedCommandService;
  private final MemberService memberService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void hideFeed(Long memberId, Long feedId) {
    Member member = memberService.getMember(memberId);
    Feed feed = feedCommandService.getFeed(feedId);

    feedHideRepository.findByMemberAndFeed(member, feed).ifPresent(hide -> {
      return;
    });

    FeedHide feedHide = FeedHide.of(member, feed);
    feedHideRepository.save(feedHide);

    eventPublisher.publishEvent(new FeedHiddenEvent(memberId, feedId));
    log.info("Published FeedHiddenEvent for memberId: {}, feedId: {}", memberId, feedId);
  }

}

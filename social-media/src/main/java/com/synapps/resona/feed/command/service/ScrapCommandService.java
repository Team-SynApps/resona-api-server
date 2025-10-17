package com.synapps.resona.feed.command.service;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.entity.Scrap;
import com.synapps.resona.feed.command.repository.ScrapRepository;
import com.synapps.resona.feed.dto.ScrapResponse;
import com.synapps.resona.feed.event.FeedScrappedEvent;
import com.synapps.resona.feed.exception.ScrapException;
import com.synapps.resona.command.service.MemberService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScrapCommandService {

  private final ScrapRepository scrapRepository;
  private final MemberService memberService;
  private final FeedCommandService feedCommandService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public ScrapResponse scrapFeed(Long memberId, Long feedId) {
    Member member = memberService.getMember(memberId);
    Feed feed = feedCommandService.getFeed(feedId);

    if (scrapRepository.findByMemberAndFeed(member, feed).isPresent()) {
      throw ScrapException.scrapAlreadyExist();
    }

    Scrap newScrap = scrapRepository.save(Scrap.of(member, feed, LocalDateTime.now()));
    eventPublisher.publishEvent(new FeedScrappedEvent(memberId, feedId, true));

    return ScrapResponse.from(newScrap);
  }

  @Transactional
  public void unscrapFeed(Long memberId, Long feedId) {
    Member member = memberService.getMember(memberId);
    Feed feed = feedCommandService.getFeed(feedId);

    scrapRepository.findByMemberAndFeed(member, feed).ifPresent(scrap -> {
      scrap.softDelete();
      eventPublisher.publishEvent(new FeedScrappedEvent(memberId, feedId, false));
    });
  }
}
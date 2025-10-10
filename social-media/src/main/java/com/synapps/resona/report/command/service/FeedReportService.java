package com.synapps.resona.report.command.service;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.service.FeedCommandService;
import com.synapps.resona.query.member.event.MemberBlockedEvent;
import com.synapps.resona.report.command.entity.FeedReport;
import com.synapps.resona.report.command.repository.FeedReportRepository;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.event.FeedReportedEvent;
import com.synapps.resona.report.exception.ReportException;
import com.synapps.resona.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedReportService {

  private final FeedReportRepository feedReportRepository;
  private final MemberService memberService;
  private final FeedCommandService feedCommandService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void reportFeed(Long reporterId, Long feedId, ReportCategory category, boolean isBlocked) {
    Member reporter = memberService.getMember(reporterId);
    Feed feed = feedCommandService.getFeed(feedId);
    Member reported = feed.getMember();

    if (feedReportRepository.existsByReporterAndFeed(reporter, feed)) {
      throw ReportException.alreadyReported();
    }

    FeedReport feedReport = FeedReport.of(reporter, reported, category, feed);
    feedReportRepository.save(feedReport);

    FeedReportedEvent reportedEvent = new FeedReportedEvent(
        feedReport.getId(),
        reporter.getId(),
        reporter.getProfile().getNickname(),
        reported.getId(),
        reported.getProfile().getNickname(),
        feedId,
        feed.getContent().substring(0, Math.min(feed.getContent().length(), 100)),
        category,
        feedReport.getCreatedAt()
    );
    eventPublisher.publishEvent(reportedEvent);

    if (isBlocked) {
      eventPublisher.publishEvent(new MemberBlockedEvent(reporterId, reported.getId()));
    }
  }
}
package com.synapps.resona.report.command.service;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.feed.exception.FeedException;
import com.synapps.resona.query.event.MemberBlockedEvent;
import com.synapps.resona.report.command.entity.FeedReport;
import com.synapps.resona.report.command.repository.FeedReportRepository;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.dto.response.FeedReportResponse;
import com.synapps.resona.report.event.FeedReportedEvent;
import com.synapps.resona.report.exception.ReportException;
import com.synapps.resona.command.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedReportService {

  private final FeedReportRepository feedReportRepository;
  private final MemberService memberService;
  private final FeedRepository feedRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public FeedReportResponse reportFeed(Long reporterId, Long feedId, ReportCategory category, boolean isBlocked) {
    Member reporter = memberService.getMember(reporterId);
    Feed feed = feedRepository.findById(feedId).orElseThrow(FeedException::feedNotFoundException);
    Member reported = feed.getMember();

    if (feedReportRepository.existsByReporterAndFeedId(reporter, feedId)) {
      throw ReportException.alreadyReported();
    }

    FeedReport feedReport = FeedReport.of(reporter, reported, category, feedId);
    feedReportRepository.save(feedReport);

    FeedReportedEvent reportedEvent = new FeedReportedEvent(
        feedReport.getId(),
        reporter.getId(),
        reporter.getProfile().getNickname(),
        reported.getId(),
        reported.getProfile().getNickname(),
        feedId,
        "", // Content is not available anymore
        category,
        feedReport.getCreatedAt()
    );
    eventPublisher.publishEvent(reportedEvent);

    if (isBlocked) {
      eventPublisher.publishEvent(new MemberBlockedEvent(reporterId, reported.getId()));
    }

    return new FeedReportResponse(feedReport);
  }
}
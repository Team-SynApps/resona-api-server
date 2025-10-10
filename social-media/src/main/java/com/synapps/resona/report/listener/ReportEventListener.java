package com.synapps.resona.report.listener;

import com.synapps.resona.report.common.entity.ReportStatus;
import com.synapps.resona.report.event.FeedReportedEvent;
import com.synapps.resona.report.event.ReportStatusChangedEvent;
import com.synapps.resona.report.query.entity.ReportDocument;
import com.synapps.resona.report.query.entity.ReportTarget;
import com.synapps.resona.report.query.entity.ReportType;
import com.synapps.resona.report.query.entity.Reported;
import com.synapps.resona.report.query.entity.Reporter;
import com.synapps.resona.report.query.repository.ReportDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportEventListener {

  private final ReportDocumentRepository reportDocumentRepository;

  @Async
  @EventListener
  public void handleFeedReported(FeedReportedEvent event) {
    log.info("Received FeedReportedEvent for originalReportId: {}", event.reportedId());

    ReportTarget target = ReportTarget.of(ReportType.FEED, event.feedId(), event.contentSnippet());
    Reporter reporter = Reporter.of(event.reporterId(), event.reporterNickname());
    Reported reportedMember = Reported.of(event.reportedId(), event.reportedNickname());

    ReportDocument reportDocument = ReportDocument.of(event.reportId(), target, reporter, reportedMember, event.category());

    reportDocumentRepository.save(reportDocument);
    log.info("Saved ReportDocument for reportId: {}", reportDocument.getReportId());
  }

  @Async
  @EventListener
  public void handleReportStatusChanged(ReportStatusChangedEvent event) {
    log.info("Received ReportStatusChangedEvent for reportId: {}", event.reportId());

    reportDocumentRepository.findByReportId(event.reportId()).ifPresent(doc -> {
      if (event.newStatus() == ReportStatus.RESOLVED) {
        doc.resolve();
      } else if (event.newStatus() == ReportStatus.REJECTED) {
        doc.reject();
      }
      reportDocumentRepository.save(doc);
      log.info("Updated ReportDocument status for reportId: {}", doc.getReportId());
    });
  }
}
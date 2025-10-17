package com.synapps.resona.report.command.service;

import com.synapps.resona.report.command.repository.ReportRepository;
import com.synapps.resona.report.command.entity.Report;
import com.synapps.resona.report.common.entity.ReportStatus;
import com.synapps.resona.report.event.ReportStatusChangedEvent;
import com.synapps.resona.report.exception.ReportException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synapps.resona.report.common.entity.ReportCategory;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportManagementService {

  private final ReportRepository reportRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void resolveReport(Long reportId) {
    Report report = reportRepository.findById(reportId)
        .orElseThrow(ReportException::reportNotFound);
    report.resolve();

    ReportCategory category = report.getCategory();
    int sanctionDays = category.getSanctionDays();

    if (sanctionDays != 0) {
      if (sanctionDays == -1) {
        report.getReported().getAccountInfo().ban(LocalDateTime.now().plusYears(100));
      } else {
        report.getReported().getAccountInfo().ban(LocalDateTime.now().plusDays(sanctionDays));
      }
    }

    eventPublisher.publishEvent(new ReportStatusChangedEvent(reportId, ReportStatus.RESOLVED));
  }

  @Transactional
  public void rejectReport(Long reportId) {
    Report report = reportRepository.findById(reportId)
        .orElseThrow(ReportException::reportNotFound);

    report.reject();

    eventPublisher.publishEvent(new ReportStatusChangedEvent(reportId, ReportStatus.REJECTED));
  }
}
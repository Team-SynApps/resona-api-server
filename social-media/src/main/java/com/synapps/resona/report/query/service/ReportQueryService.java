package com.synapps.resona.report.query.service;

import com.synapps.resona.report.common.entity.ReportStatus;
import com.synapps.resona.report.dto.ReportDto;
import com.synapps.resona.report.query.repository.ReportDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportQueryService {

  private final ReportDocumentRepository reportDocumentRepository;

  public Page<ReportDto> getReportsByStatus(ReportStatus status, Pageable pageable) {
    return reportDocumentRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
        .map(ReportDto::from);
  }
}
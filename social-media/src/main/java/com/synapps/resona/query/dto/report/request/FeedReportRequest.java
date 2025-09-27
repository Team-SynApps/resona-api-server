package com.synapps.resona.query.dto.report.request;

import com.synapps.resona.domain.entity.report.ReportCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FeedReportRequest {

  private Long reportedId;

  private Long feedId;

  private ReportCategory reportCategory;

}

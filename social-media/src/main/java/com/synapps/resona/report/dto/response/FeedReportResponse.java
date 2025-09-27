package com.synapps.resona.report.dto.response;

import com.synapps.resona.report.entity.FeedReport;
import com.synapps.resona.report.entity.ReportCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class FeedReportResponse {

  private Long id;

  private Long reporterId;

  private Long reportedId;

  private ReportCategory category;

  private Long feedId;

  public static FeedReportResponse from(FeedReport report) {
    return FeedReportResponse.of(
        report.getId(),
        report.getReporter().getId(),
        report.getReported().getId(),
        report.getCategory(),
        report.getFeed().getId());
  }
}

package com.synapps.resona.report.dto.response;

import com.synapps.resona.report.command.entity.CommentReport;
import com.synapps.resona.report.common.entity.ReportCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class CommentReportResponse {

  private Long id;

  private Long reporterId;

  private Long reportedId;

  private ReportCategory category;

  private Long commentId;

  public static CommentReportResponse from(CommentReport report) {
    return CommentReportResponse.of(
        report.getId(),
        report.getReporter().getId(),
        report.getReported().getId(),
        report.getCategory(),
        report.getComment().getId());
  }
}

package com.synapps.resona.report.dto.request;

import com.synapps.resona.report.entity.ReportCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentReportRequest {

  private Long commentId;

  private Long reportedId;

  private ReportCategory reportCategory;

}

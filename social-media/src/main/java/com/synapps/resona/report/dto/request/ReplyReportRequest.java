package com.synapps.resona.report.dto.request;

import com.synapps.resona.report.entity.ReportCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ReplyReportRequest {

  private Long reportedId;

  private Long replyId;

  private ReportCategory reportCategory;

}

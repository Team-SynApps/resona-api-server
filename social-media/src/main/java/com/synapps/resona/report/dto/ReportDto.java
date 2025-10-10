package com.synapps.resona.report.dto;

import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.common.entity.ReportStatus;
import com.synapps.resona.report.query.entity.ReportDocument;
import com.synapps.resona.report.query.entity.ReportTarget;
import com.synapps.resona.report.query.entity.Reported;
import com.synapps.resona.report.query.entity.Reporter;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
@Builder
public class ReportDto {
  private ObjectId _id;
  private Long reportId;
  private ReportTarget target;
  private Reporter reporter;
  private Reported reportedMember;
  private ReportCategory category;
  private ReportStatus status;
  private LocalDateTime createdAt;

  public static ReportDto from(ReportDocument document) {
    return ReportDto.builder()
        ._id(document.get_id())
        .reportId(document.getReportId())
        .target(document.getTarget())
        .reporter(document.getReporter())
        .reportedMember(document.getReportedMember())
        .category(document.getCategory())
        .status(document.getStatus())
        .createdAt(document.getCreatedAt())
        .build();
  }
}
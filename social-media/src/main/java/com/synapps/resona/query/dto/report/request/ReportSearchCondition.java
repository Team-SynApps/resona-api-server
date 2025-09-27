package com.synapps.resona.query.dto.report.request;

import com.synapps.resona.domain.entity.report.ReportCategory;
import lombok.Data;

@Data
public class ReportSearchCondition {

  private String reportType;

  private ReportCategory category;
}

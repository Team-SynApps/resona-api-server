package com.synapps.resona.report.dto.request;

import com.synapps.resona.report.entity.ReportCategory;
import lombok.Data;

@Data
public class ReportSearchCondition {

  private String reportType;

  private ReportCategory category;
}

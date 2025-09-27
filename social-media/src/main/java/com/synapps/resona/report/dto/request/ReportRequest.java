package com.synapps.resona.report.dto.request;

import com.synapps.resona.report.entity.ReportCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequest {

  private ReportCategory reportCategory;

  private boolean isBlocked;
}

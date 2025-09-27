package com.synapps.resona.query.dto.report.request;

import com.synapps.resona.domain.entity.report.ReportCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequest {

  private ReportCategory reportCategory;

  private boolean isBlocked;
}

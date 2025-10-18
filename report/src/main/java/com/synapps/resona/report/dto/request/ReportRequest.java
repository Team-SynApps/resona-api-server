package com.synapps.resona.report.dto.request;

import com.synapps.resona.report.common.entity.ReportCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequest {

  @NotNull(message = "신고 사유는 필수입니다.")
  private ReportCategory reportCategory;

  private boolean isBlocked;
}

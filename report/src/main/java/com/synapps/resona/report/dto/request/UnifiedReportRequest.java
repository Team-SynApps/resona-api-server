package com.synapps.resona.report.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synapps.resona.report.common.entity.ReportCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UnifiedReportRequest {

    @NotNull
    private ReportType reportType;

    @NotNull
    private Long targetId;

    @NotNull
    private ReportCategory category;

    @JsonProperty("blocked")
    private boolean isBlocked;
}

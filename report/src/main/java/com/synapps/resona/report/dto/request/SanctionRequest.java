package com.synapps.resona.report.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SanctionRequest {

    @NotNull
    private Long memberId;

    @NotNull
    private Integer sanctionDays;
}

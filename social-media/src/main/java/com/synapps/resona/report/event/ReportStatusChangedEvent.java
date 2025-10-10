package com.synapps.resona.report.event;

import com.synapps.resona.report.common.entity.ReportStatus;

public record ReportStatusChangedEvent(
    Long reportId,
    ReportStatus newStatus
) {
}
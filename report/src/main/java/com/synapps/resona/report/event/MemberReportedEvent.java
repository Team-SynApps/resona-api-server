package com.synapps.resona.report.event;

import com.synapps.resona.report.common.entity.ReportCategory;
import java.time.LocalDateTime;

public record MemberReportedEvent(
    Long reportId,
    Long reporterId,
    String reporterNickname,
    Long reportedId,
    String reportedNickname,
    ReportCategory category,
    LocalDateTime createdAt
) {
}

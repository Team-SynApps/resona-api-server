package com.synapps.resona.report.event;

import com.synapps.resona.report.common.entity.ReportCategory;
import java.time.LocalDateTime;

public record CommentReportedEvent(
    Long reportId,
    Long reporterId,
    String reporterNickname,
    Long reportedId,
    String reportedNickname,
    Long commentId,
    String contentSnippet,
    ReportCategory category,
    LocalDateTime createdAt
) {
}

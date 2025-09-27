package com.synapps.resona.event;

public record ReportCreatedEvent(
    Long reportId,
    String reportType,
    Long reporterId,
    Long reportedId,
    Long contentId,
    String reportCategoryDescription
) {
}

package com.synapps.resona.report.dto.response;

import com.synapps.resona.report.command.entity.FeedReport;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.common.entity.ReportStatus;
import lombok.Getter;

@Getter
public class FeedReportResponse {

    private final Long reportId;
    private final Long reporterId;
    private final Long reportedId;
    private final Long feedId;
    private final ReportCategory category;
    private final ReportStatus status;

    public FeedReportResponse(FeedReport feedReport) {
        this.reportId = feedReport.getId();
        this.reporterId = feedReport.getReporter().getId();
        this.reportedId = feedReport.getReported().getId();
        this.feedId = feedReport.getFeedId();
        this.category = feedReport.getCategory();
        this.status = feedReport.getReportStatus();
    }
}
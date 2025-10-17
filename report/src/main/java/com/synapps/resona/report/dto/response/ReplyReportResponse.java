package com.synapps.resona.report.dto.response;

import com.synapps.resona.report.command.entity.ReplyReport;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.common.entity.ReportStatus;
import lombok.Getter;

@Getter
public class ReplyReportResponse {

    private final Long reportId;
    private final Long reporterId;
    private final Long reportedId;
    private final Long replyId;
    private final ReportCategory category;
    private final ReportStatus status;

    public ReplyReportResponse(ReplyReport replyReport) {
        this.reportId = replyReport.getId();
        this.reporterId = replyReport.getReporter().getId();
        this.reportedId = replyReport.getReported().getId();
        this.replyId = replyReport.getReplyId();
        this.category = replyReport.getCategory();
        this.status = replyReport.getReportStatus();
    }
}
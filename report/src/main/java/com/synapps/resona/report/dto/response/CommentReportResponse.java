package com.synapps.resona.report.dto.response;

import com.synapps.resona.report.command.entity.CommentReport;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.common.entity.ReportStatus;
import lombok.Getter;

@Getter
public class CommentReportResponse {

    private final Long reportId;
    private final Long reporterId;
    private final Long reportedId;
    private final Long commentId;
    private final ReportCategory category;
    private final ReportStatus status;

    public CommentReportResponse(CommentReport commentReport) {
        this.reportId = commentReport.getId();
        this.reporterId = commentReport.getReporter().getId();
        this.reportedId = commentReport.getReported().getId();
        this.commentId = commentReport.getCommentId();
        this.category = commentReport.getCategory();
        this.status = commentReport.getReportStatus();
    }
}
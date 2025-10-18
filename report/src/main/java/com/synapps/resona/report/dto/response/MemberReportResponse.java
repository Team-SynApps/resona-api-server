package com.synapps.resona.report.dto.response;

import com.synapps.resona.report.command.entity.MemberReport;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.common.entity.ReportStatus;
import lombok.Getter;

@Getter
public class MemberReportResponse {

    private final Long reportId;
    private final Long reporterId;
    private final Long reportedId;
    private final ReportCategory category;
    private final ReportStatus status;

    public MemberReportResponse(MemberReport memberReport) {
        this.reportId = memberReport.getId();
        this.reporterId = memberReport.getReporter().getId();
        this.reportedId = memberReport.getReported().getId();
        this.category = memberReport.getCategory();
        this.status = memberReport.getReportStatus();
    }
}

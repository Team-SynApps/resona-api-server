package com.synapps.resona.report.command.entity;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.common.entity.ReportStatus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@DiscriminatorValue("MEMBER")
public class MemberReport extends Report {

    private MemberReport(Member reporter, Member reported, ReportCategory category) {
        this.setReporter(reporter);
        this.setReported(reported);
        this.setCategory(category);
        this.setReportStatus(ReportStatus.PENDING);
    }

    public static MemberReport of(Member reporter, Member reported, ReportCategory category) {
        return new MemberReport(reporter, reported, category);
    }
}

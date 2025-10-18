package com.synapps.resona.report.command.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.report.command.entity.FeedReport;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface FeedReportRepository extends JpaRepository<FeedReport, Long> {
    boolean existsByReporterAndFeedId(Member reporter, Long feedId);
}

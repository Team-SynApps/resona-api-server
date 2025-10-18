package com.synapps.resona.report.command.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.report.command.entity.MemberReport;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface MemberReportRepository extends JpaRepository<MemberReport, Long> {
    boolean existsByReporterAndReported(Member reporter, Member reported);
}

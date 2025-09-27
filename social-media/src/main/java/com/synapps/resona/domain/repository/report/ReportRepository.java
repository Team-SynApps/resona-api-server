package com.synapps.resona.domain.repository.report;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.domain.entity.report.Report;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface ReportRepository extends JpaRepository<Report, Long> {

}

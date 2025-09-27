package com.synapps.resona.report.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface ReportRepository extends JpaRepository<Report, Long> {

}

package synapps.resona.api.socialMedia.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.socialMedia.report.entity.Report;

@MySQLRepository
public interface ReportRepository extends JpaRepository<Report, Long> {

}

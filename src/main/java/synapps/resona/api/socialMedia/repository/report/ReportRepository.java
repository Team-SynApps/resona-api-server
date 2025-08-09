package synapps.resona.api.socialMedia.repository.report;

import org.springframework.data.jpa.repository.JpaRepository;
import synapps.resona.api.global.annotation.DatabaseRepositories.MySQLRepository;
import synapps.resona.api.socialMedia.entity.report.Report;

@MySQLRepository
public interface ReportRepository extends JpaRepository<Report, Long> {

}

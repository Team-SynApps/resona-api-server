package synapps.resona.api.mysql.socialMedia.repository.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.socialMedia.entity.report.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

}

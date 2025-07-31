package synapps.resona.api.mysql.socialMedia.repository.complaint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.socialMedia.entity.report.FeedReport;

@Repository
public interface FeedComplaintRepository extends JpaRepository<FeedReport, Long> {

}

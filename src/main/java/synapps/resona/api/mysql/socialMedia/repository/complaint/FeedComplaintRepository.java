package synapps.resona.api.mysql.socialMedia.repository.complaint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synapps.resona.api.mysql.socialMedia.entity.complaint.FeedComplaint;

@Repository
public interface FeedComplaintRepository extends JpaRepository<FeedComplaint, Long> {

}

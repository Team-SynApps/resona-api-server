package synapps.resona.api.mysql.socialMedia.dto.complaint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.report.ReportCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedComplaintRequest {

  private ReportCategory reportCategory;
  private boolean isBlocked;
}

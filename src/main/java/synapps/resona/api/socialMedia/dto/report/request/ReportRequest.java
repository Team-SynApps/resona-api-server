package synapps.resona.api.socialMedia.dto.report.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.entity.report.ReportCategory;

@Getter
@NoArgsConstructor
public class ReportRequest {

  private ReportCategory reportCategory;

  private boolean isBlocked;
}

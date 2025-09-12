package synapps.resona.api.socialMedia.report.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.report.entity.ReportCategory;

@Getter
@NoArgsConstructor
public class ReportRequest {

  private ReportCategory reportCategory;

  private boolean isBlocked;
}

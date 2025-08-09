package synapps.resona.api.socialMedia.dto.report.request;

import lombok.Data;
import synapps.resona.api.socialMedia.entity.report.ReportCategory;

@Data
public class ReportSearchCondition {

  private String reportType;

  private ReportCategory category;
}

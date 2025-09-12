package synapps.resona.api.socialMedia.report.dto.request;

import lombok.Data;
import synapps.resona.api.socialMedia.report.entity.ReportCategory;

@Data
public class ReportSearchCondition {

  private String reportType;

  private ReportCategory category;
}

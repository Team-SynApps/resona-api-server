package synapps.resona.api.mysql.socialMedia.dto.report.request;

import lombok.Data;
import synapps.resona.api.mysql.socialMedia.entity.report.ReportCategory;

@Data
public class ReportSearchCondition {

  private String reportType;

  private ReportCategory category;
}

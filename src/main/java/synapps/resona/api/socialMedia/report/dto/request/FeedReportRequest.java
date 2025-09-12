package synapps.resona.api.socialMedia.report.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.socialMedia.report.entity.ReportCategory;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FeedReportRequest {

  private Long reportedId;

  private Long feedId;

  private ReportCategory reportCategory;

}

package synapps.resona.api.mysql.socialMedia.dto.report.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.report.ReportCategory;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FeedReportRequest {

  private Long reportedId;

  private Long feedId;

  private ReportCategory reportCategory;

}

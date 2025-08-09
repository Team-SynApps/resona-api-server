package synapps.resona.api.socialMedia.dto.report.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.socialMedia.entity.report.ReportCategory;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentReportRequest {

  private Long commentId;

  private Long reportedId;

  private ReportCategory reportCategory;

}

package synapps.resona.api.socialMedia.report.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.socialMedia.report.entity.ReportCategory;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentReportRequest {

  private Long commentId;

  private Long reportedId;

  private ReportCategory reportCategory;

}

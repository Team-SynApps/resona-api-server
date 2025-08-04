package synapps.resona.api.mysql.socialMedia.dto.report.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.report.ReportCategory;

@Getter
@NoArgsConstructor
public class CommentReportRequest {

  private Long commentId;

  private Long reportedId;

  private ReportCategory reportCategory;

}

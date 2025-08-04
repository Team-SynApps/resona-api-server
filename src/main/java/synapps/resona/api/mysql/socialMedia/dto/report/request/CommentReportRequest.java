package synapps.resona.api.mysql.socialMedia.dto.report.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.report.ReportCategory;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentReportRequest {

  private Long commentId;

  private Long reportedId;

  private ReportCategory reportCategory;

}

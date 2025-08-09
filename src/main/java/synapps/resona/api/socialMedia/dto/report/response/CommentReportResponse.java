package synapps.resona.api.socialMedia.dto.report.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.entity.report.CommentReport;
import synapps.resona.api.socialMedia.entity.report.ReportCategory;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class CommentReportResponse {

  private Long id;

  private Long reporterId;

  private Long reportedId;

  private ReportCategory category;

  private Long commentId;

  public static CommentReportResponse from(CommentReport report) {
    return CommentReportResponse.of(
        report.getId(),
        report.getReporter().getId(),
        report.getReported().getId(),
        report.getCategory(),
        report.getComment().getId());
  }
}

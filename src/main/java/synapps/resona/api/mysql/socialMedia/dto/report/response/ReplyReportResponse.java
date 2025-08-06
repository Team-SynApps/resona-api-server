package synapps.resona.api.mysql.socialMedia.dto.report.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.report.CommentReport;
import synapps.resona.api.mysql.socialMedia.entity.report.ReplyReport;
import synapps.resona.api.mysql.socialMedia.entity.report.ReportCategory;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class ReplyReportResponse {

  private Long id;

  private Long reporterId;

  private Long reportedId;

  private ReportCategory category;

  private Long replyId;

  public static ReplyReportResponse from(ReplyReport report) {
    return ReplyReportResponse.of(
        report.getId(),
        report.getReporter().getId(),
        report.getReported().getId(),
        report.getCategory(),
        report.getReply().getId());
  }
}

package synapps.resona.api.socialMedia.report.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.report.entity.ReplyReport;
import synapps.resona.api.socialMedia.report.entity.ReportCategory;

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

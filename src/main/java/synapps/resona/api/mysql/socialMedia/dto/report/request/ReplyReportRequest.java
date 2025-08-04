package synapps.resona.api.mysql.socialMedia.dto.report.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.report.ReportCategory;

@Getter
@NoArgsConstructor
public class ReplyReportRequest {

  private Long reportedId;

  private Long replyId;

  private ReportCategory reportCategory;

}

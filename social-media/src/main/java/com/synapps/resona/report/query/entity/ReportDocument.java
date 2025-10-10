package com.synapps.resona.report.query.entity;

import com.synapps.resona.entity.BaseDocument;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.common.entity.ReportStatus;
import com.synapps.resona.report.exception.ReportException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "reports")
public class ReportDocument extends BaseDocument {

  @Id
  private ObjectId _id;

  @Indexed
  private Long reportId;

  @Indexed
  private ReportTarget target;

  private Reporter reporter;

  private Reported reportedMember;

  @Indexed
  private ReportCategory category;

  @Indexed
  private ReportStatus status;

  private ReportDocument(Long reportId, ReportTarget target, Reporter reporter,
      Reported reportedMember, ReportCategory category) {
    this.reportId = reportId;
    this.target = target;
    this.reporter = reporter;
    this.reportedMember = reportedMember;
    this.category = category;
    this.status = ReportStatus.PENDING;
  }

  public static ReportDocument of(Long reportId, ReportTarget target, Reporter reporter,
      Reported reportedMember, ReportCategory category) {
    return new ReportDocument(reportId, target, reporter, reportedMember, category);
  }

  public void resolve() {
    if (this.status != ReportStatus.PENDING) {
      throw ReportException.alreadyExecuted();
    }
    this.status = ReportStatus.RESOLVED;
  }

  public void reject() {
    if (this.status != ReportStatus.PENDING) {
      throw ReportException.alreadyExecuted();
    }
    this.status = ReportStatus.REJECTED;
  }

}
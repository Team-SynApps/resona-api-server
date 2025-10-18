package com.synapps.resona.report.command.entity;

import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.report.common.entity.ReportStatus;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@DiscriminatorValue("REPLY")
public class ReplyReport extends Report {

  @Column(name = "reply_id")
  private Long replyId;

  private ReplyReport(Member reporter, Member reported, Long replyId, ReportCategory category) {
    this.setReporter(reporter);
    this.setReported(reported);
    this.setCategory(category);
    this.setReportStatus(ReportStatus.PENDING);
    this.replyId = replyId;
  }

  /**
   * ReplyReport 엔티티를 생성하는 정적 팩토리 메서드.
   * @param reporter 신고자
   * @param reported 피신고자
   * @param replyId 신고 대상 대댓글 ID
   * @param category 신고 사유
   * @return 생성된 ReplyReport 객체
   */
  public static ReplyReport of(Member reporter, Member reported, Long replyId, ReportCategory category) {
    return new ReplyReport(reporter, reported, replyId, category);
  }
}

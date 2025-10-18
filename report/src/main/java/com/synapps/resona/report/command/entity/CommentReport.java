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
@DiscriminatorValue("COMMENT")
public class CommentReport extends Report {

  @Column(name = "comment_id")
  private Long commentId;

  private CommentReport(Member reporter, Member reported, Long commentId, ReportCategory category) {
    this.setReporter(reporter);
    this.setReported(reported);
    this.setCategory(category);
    this.setReportStatus(ReportStatus.PENDING);
    this.commentId = commentId;
  }

  /**
   * CommentReport 엔티티를 생성하는 정적 팩토리 메서드
   * @param reporter 신고자
   * @param reported 피신고자
   * @param commentId 신고 대상 댓글 ID
   * @param category 신고 사유
   * @return 생성된 CommentReport 객체
   */
  public static CommentReport of(Member reporter, Member reported, Long commentId, ReportCategory category) {
    return new CommentReport(reporter, reported, commentId, category);
  }
}

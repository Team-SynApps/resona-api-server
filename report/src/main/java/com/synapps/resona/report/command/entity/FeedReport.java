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
@DiscriminatorValue("FEED")
public class FeedReport extends Report {

  @Column(name = "feed_id")
  private Long feedId;

  private FeedReport(Member reporter, Member reported, ReportCategory category, Long targetFeedId) {
    this.setReporter(reporter);
    this.setReported(reported);
    this.setCategory(category);
    this.setReportStatus(ReportStatus.PENDING);
    this.feedId = targetFeedId;
  }

  /**
   * FeedReport 엔티티를 생성하는 정적 팩토리 메서드.
   * @param reporter 신고자
   * @param reported 피신고자
   * @param targetFeedId 신고 대상 피드 ID
   * @param category 신고 사유
   * @return 생성된 FeedReport 객체
   */
  public static FeedReport of(Member reporter, Member reported, ReportCategory category,
      Long targetFeedId) {
    return new FeedReport(reporter, reported, category, targetFeedId);
  }

}

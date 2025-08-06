package synapps.resona.api.mysql.socialMedia.entity.report;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@DiscriminatorValue("FEED")
public class FeedReport extends Report {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "feed_id")
  private Feed feed;

  private FeedReport(Member reporter, Member reported, ReportCategory category, Feed targetFeed) {
    this.setReporter(reporter);
    this.setReported(reported);
    this.setCategory(category);
    this.feed = targetFeed;
  }

  /**
   * FeedReport 엔티티를 생성하는 정적 팩토리 메서드.
   * @param reporter 신고자
   * @param reported 피신고자
   * @param targetFeed 신고 대상 피드
   * @param category 신고 사유
   * @return 생성된 FeedReport 객체
   */
  public static FeedReport of(Member reporter, Member reported, ReportCategory category,
      Feed targetFeed) {
    return new FeedReport(reporter, reported, category, targetFeed);
  }

}

package com.synapps.resona.report.entity;

import com.synapps.resona.comment.entity.Comment;
import com.synapps.resona.entity.member.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id")
  private Comment comment;

  private CommentReport(Member reporter, Member reported, Comment comment, ReportCategory category) {
    this.setReporter(reporter);
    this.setReported(reported);
    this.setCategory(category);
    this.comment = comment;
  }

  /**
   * CommentReport 엔티티를 생성하는 정적 팩토리 메서드.
   * @param reporter 신고자
   * @param reported 피신고자
   * @param comment 신고 대상 댓글
   * @param category 신고 사유
   * @return 생성된 CommentReport 객체
   */
  public static CommentReport of(Member reporter, Member reported, Comment comment, ReportCategory category) {
    return new CommentReport(reporter, reported, comment, category);
  }
}

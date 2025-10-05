package com.synapps.resona.report.command.entity;

import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.report.common.entity.ReportCategory;
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
@DiscriminatorValue("REPLY")
public class ReplyReport extends Report {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reply_id")
  private Reply reply;

  private ReplyReport(Member reporter, Member reported, Reply reply, ReportCategory category) {
    this.setReporter(reporter);
    this.setReported(reported);
    this.setCategory(category);
    this.reply = reply;
  }

  /**
   * ReplyReport 엔티티를 생성하는 정적 팩토리 메서드.
   * @param reporter 신고자
   * @param reported 피신고자
   * @param reply 신고 대상 대댓글
   * @param category 신고 사유
   * @return 생성된 ReplyReport 객체
   */
  public static ReplyReport of(Member reporter, Member reported, Reply reply, ReportCategory category) {
    return new ReplyReport(reporter, reported, reply, category);
  }
}

package com.synapps.resona.comment.command.entity.reply;

import com.synapps.resona.common.entity.Hide;
import com.synapps.resona.entity.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@DiscriminatorValue("REPLY")
public class ReplyHide extends Hide {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "reply_id")
  private Reply reply;

  private ReplyHide(Member member, Reply reply) {
    this.setMember(member);
    this.reply = reply;
  }

  /**
   * ReplyHide 엔티티를 생성하는 정적 팩토리 메서드.
   * @param member 숨김 처리한 사용자
   * @param reply 숨김 대상 대댓글
   * @return 생성된 ReplyHide 객체
   */
  public static ReplyHide of(Member member, Reply reply) {
    return new ReplyHide(member, reply);
  }
}
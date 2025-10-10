package com.synapps.resona.comment.command.entity.reply;

import com.synapps.resona.common.entity.Likes;
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
public class ReplyLikes extends Likes {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "reply_id")
  private Reply reply;

  private ReplyLikes(Member member, Reply reply) {
    this.setMember(member);
    this.reply = reply;
  }

  public static ReplyLikes of(Member member, Reply reply) {
    return new ReplyLikes(member, reply);
  }
}
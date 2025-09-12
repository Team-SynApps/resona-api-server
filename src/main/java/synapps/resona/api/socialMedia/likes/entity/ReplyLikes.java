package synapps.resona.api.socialMedia.likes.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.comment.entity.Reply;

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
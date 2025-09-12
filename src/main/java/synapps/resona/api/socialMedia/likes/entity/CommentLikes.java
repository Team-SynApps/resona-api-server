package synapps.resona.api.socialMedia.likes.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.comment.entity.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
@DiscriminatorValue("COMMENT")
public class CommentLikes extends Likes {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "comment_id")
  private Comment comment;

  private CommentLikes(Member member, Comment comment) {
    this.setMember(member);
    this.comment = comment;
  }

  public static CommentLikes of(Member member, Comment comment) {
    return new CommentLikes(member, comment);
  }
}
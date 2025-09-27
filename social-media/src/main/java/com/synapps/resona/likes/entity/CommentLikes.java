package com.synapps.resona.likes.entity;

import com.synapps.resona.comment.entity.Comment;
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
package com.synapps.resona.comment.command.entity.reply;

import com.synapps.resona.comment.command.entity.MentionableContent;
import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.entity.Language;
import com.synapps.resona.entity.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("REPLY")
public class Reply extends MentionableContent {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id")
  private Comment comment;

  @Column(name = "like_count")
  private long likeCount = 0L;

  private Reply(Comment comment, Member member, String languageCode, String content) {
    this.comment = comment;
    this.setMember(member);
    this.setLanguage(Language.fromCode(languageCode));
    this.setContent(content);
  }

  public static Reply of(Comment comment, Member member, String languageCode, String content) {
    return new Reply(comment, member, languageCode, content);
  }

  public void increaseLikeCount() { this.likeCount++; }
  public void decreaseLikeCount() { if (this.likeCount > 0) this.likeCount--; }
}

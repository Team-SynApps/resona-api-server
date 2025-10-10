package com.synapps.resona.comment.command.entity.comment;

import com.synapps.resona.comment.command.entity.MentionableContent;
import com.synapps.resona.entity.Language;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("COMMENT")
public class Comment extends MentionableContent {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "feed_id")
  private Feed feed;

  @Column(name = "reply_count")
  private long replyCount = 0L;

  @Column(name = "like_count")
  private long likeCount = 0L;

  private Comment(Feed feed, Member member, String languageCode, String content) {
    this.feed = feed;
    this.setMember(member);
    this.setLanguage(Language.fromCode(languageCode));
    this.setContent(content);
  }

  public static Comment of(Feed feed, Member member, String languageCode, String content) {
    return new Comment(feed, member, languageCode, content);
  }

  public void increaseReplyCount() { this.replyCount++; }
  public void decreaseReplyCount() { if (this.replyCount > 0) this.replyCount--; }

  public void increaseLikeCount() { this.likeCount++; }
  public void decreaseLikeCount() { if (this.likeCount > 0) this.likeCount--; }
}

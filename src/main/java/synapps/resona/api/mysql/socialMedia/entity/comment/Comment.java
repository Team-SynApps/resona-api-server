package synapps.resona.api.mysql.socialMedia.entity.comment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.socialMedia.entity.mention.Mention;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Comment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "comment_id")
  private Long id;

  @OneToMany(mappedBy = "comment")
  private final List<Reply> replies = new ArrayList<>();

  @OneToMany(mappedBy = "comment")
  private final List<Mention> mentions = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "feed_id")
  private Feed feed;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Column(name = "content")
  private String content;

  @Column(name = "is_reply_exist")
  private boolean isReplyExist = false;

  private Comment(Feed feed, Member member, String content) {
    this.feed = feed;
    this.member = member;
    this.content = content;
  }

  public static Comment of(Feed feed, Member member, String content) {
    return new Comment(feed, member, content);
  }

  public void addReply() {
    this.isReplyExist = true;
  }

  public void removeReply() {
    this.isReplyExist = false;
  }

  public void updateContent(String content) {
    this.content = content;
  }
}

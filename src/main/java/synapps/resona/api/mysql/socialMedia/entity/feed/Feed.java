package synapps.resona.api.mysql.socialMedia.entity.feed;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;
import synapps.resona.api.mysql.socialMedia.entity.complaint.FeedComplaint;
import synapps.resona.api.mysql.socialMedia.entity.media.FeedMedia;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "feed_id")
  private Long id;

  @OneToMany(mappedBy = "feed", cascade = CascadeType.PERSIST)
  private final List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "feed", cascade = CascadeType.PERSIST)
  private final List<Likes> likes = new ArrayList<>();

  @OneToMany(mappedBy = "feed", cascade = CascadeType.PERSIST)
  private final List<FeedMedia> images = new ArrayList<>();

  @OneToMany(mappedBy = "feed")
  private final List<FeedComplaint> complaints = new ArrayList<>();

  @Column(name = "is_kept")
  private final boolean isKept = false;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @Column(name = "content")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(name = "category")
  private FeedCategory category;

  private Feed(Member member, String content, String category) {
    this.member = member;
    this.content = content;
    this.category = FeedCategory.of(category);
  }

  public static Feed of(Member member, String content, String category) {
    return new Feed(member, content, category);
  }

  public void updateContent(String content) {
    this.content = content;
  }
}

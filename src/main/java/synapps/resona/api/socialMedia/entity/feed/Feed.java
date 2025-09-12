package synapps.resona.api.socialMedia.entity.feed;

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
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.global.entity.Language;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.socialMedia.entity.comment.Comment;
import synapps.resona.api.socialMedia.entity.likes.FeedLikes;
import synapps.resona.api.socialMedia.entity.report.FeedReport;
import synapps.resona.api.socialMedia.entity.media.FeedMedia;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Feed extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "feed_id")
  private Long id;

  @OneToMany(mappedBy = "feed", cascade = CascadeType.PERSIST)
  private final List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "feed", cascade = CascadeType.PERSIST)
  private final List<FeedLikes> likes = new ArrayList<>();

  @BatchSize(size = 100)
  @OneToMany(mappedBy = "feed", cascade = CascadeType.PERSIST)
  private final List<FeedMedia> images = new ArrayList<>();

  @OneToMany(mappedBy = "feed")
  private final List<FeedReport> complaints = new ArrayList<>();

  @Column(name = "is_kept")
  private final boolean isKept = false;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @Column(name = "content")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(name = "language")
  private Language language;

  @Enumerated(EnumType.STRING)
  @Column(name = "category")
  private FeedCategory category;

  private Feed(Member member, String content, String category, String language) {
    this.member = member;
    this.content = content;
    this.category = FeedCategory.of(category);
    this.language = Language.of(language);
  }

  public static Feed of(Member member, String content, String category, String language) {
    return new Feed(member, content, category, language);
  }

  public void updateContent(String content) {
    this.content = content;
  }
}

package com.synapps.resona.domain.entity.feed;

import com.synapps.resona.domain.entity.comment.Comment;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.entity.BaseEntity;
import com.synapps.resona.entity.Language;
import com.synapps.resona.domain.entity.likes.FeedLikes;
import com.synapps.resona.domain.entity.media.FeedMedia;
import com.synapps.resona.domain.entity.report.FeedReport;
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

  private Feed(Member member, String content, String category, String languageCode) {
    this.member = member;
    this.content = content;
    this.category = FeedCategory.of(category);
    this.language = Language.fromCode(languageCode);
  }

  public static Feed of(Member member, String content, String category, String languageCode) {
    return new Feed(member, content, category, languageCode);
  }

  public void updateContent(String content) {
    this.content = content;
  }
}

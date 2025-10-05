package com.synapps.resona.feed.command.entity;

import com.synapps.resona.entity.BaseEntity;
import com.synapps.resona.entity.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class FeedTranslation extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "feed_translation_id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "language")
  private Language language;

  @ManyToOne
  @JoinColumn(name = "feed_id")
  private Feed feed;

  @Column(name="content")
  private String content;

  private FeedTranslation(Feed feed, String languageCode, String content) {
    this.feed = feed;
    this.language = Language.fromCode(languageCode);
    this.content = content;
  }

  // TODO: 생성시, 작성된 content가 LanguageCode와 일치하는지 확인할 검증 메서드가 추가되면 좋을 것 같음. Lingua 참고
}
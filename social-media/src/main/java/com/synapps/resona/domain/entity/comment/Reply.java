package com.synapps.resona.domain.entity.comment;

import com.synapps.resona.entity.Language;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
public class Reply extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reply_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id")
  private Comment comment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Enumerated(EnumType.STRING)
  @Column(name = "language")
  private Language language;

  @Column(name = "content")
  private String content;

  private Reply(Comment comment, Member member, String languageCode, String content) {
    this.comment = comment;
    this.member = member;
    this.language = Language.fromCode(languageCode);
    this.content = content;
  }

  public static Reply of(Comment comment, Member member, String languageCode, String content) {
    return new Reply(comment, member, languageCode, content);
  }

  public void update(String content) {
    this.content = content;
  }
}

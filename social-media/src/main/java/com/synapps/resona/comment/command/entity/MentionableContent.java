package com.synapps.resona.comment.command.entity;

import com.synapps.resona.entity.BaseEntity;
import com.synapps.resona.entity.Language;
import com.synapps.resona.command.entity.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@SQLRestriction("is_deleted = false")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "mentionable_type")
public abstract class MentionableContent extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Enumerated(EnumType.STRING)
  @Column(name = "language")
  private Language language;

  @Column(name = "content")
  private String content;

  protected void setMember(Member member) {
    this.member = member;
  }

  protected void setLanguage(Language language) {
    this.language = language;
  }

  protected void setContent(String content) {
    this.content = content;
  }
}
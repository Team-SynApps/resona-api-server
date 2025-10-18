package com.synapps.resona.comment.command.entity;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Mention extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "mention_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member mentionedMember;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentionable_content_id", nullable = false)
  private MentionableContent target;

  private Mention(Member mentionedMember, MentionableContent target) {
    this.mentionedMember = mentionedMember;
    this.target = target;
  }

  public static Mention of(Member member, MentionableContent target) {
    return new Mention(member, target);
  }
}
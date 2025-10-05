package com.synapps.resona.comment.query.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentionedMember {

  private Long memberId;
  private String nickname;

  private MentionedMember(Long memberId, String nickname) {
    this.memberId = memberId;
    this.nickname = nickname;
  }

  public static MentionedMember of(Long memberId, String nickname) {
    return new MentionedMember(memberId, nickname);
  }
}
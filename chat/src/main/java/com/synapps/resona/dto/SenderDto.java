package com.synapps.resona.dto;

import com.synapps.resona.entity.ChatMember;
import lombok.Getter;
@Getter
public class SenderDto {
  private final Long memberId;
  private final String nickname;
  private final String profileImageUrl;

  private SenderDto(ChatMember member) {
    this.memberId = member.getId();
    this.nickname = member.getNickname();
    this.profileImageUrl = member.getProfileImageUrl();
  }

  public static SenderDto from(ChatMember member) {
    return new SenderDto(member);
  }
}
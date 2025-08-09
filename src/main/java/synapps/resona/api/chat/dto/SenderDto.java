package synapps.resona.api.chat.dto;

import lombok.Getter;
import synapps.resona.api.chat.entity.ChatMember;

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
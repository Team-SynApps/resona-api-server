package synapps.resona.api.socialMedia.feed.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.member.entity.member.Member;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class SocialMemberDto {
  private Long memberId;
  private String nickname;
  private String profileImageUrl;

  public static SocialMemberDto from(Member member) {
    return SocialMemberDto.of(
        member.getId(),
        member.getProfile().getNickname(),
        member.getProfile().getProfileImageUrl());
  }
}

package synapps.resona.api.socialMedia.dto.feed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.member.entity.member.Member;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class FeedMemberDto {
  private Long memberId;
  private String nickname;
  private String profileImageUrl;

  public static FeedMemberDto from(Member member) {
    return FeedMemberDto.of(
        member.getId(),
        member.getProfile().getNickname(),
        member.getProfile().getProfileImageUrl());
  }
}

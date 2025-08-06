package synapps.resona.api.mysql.socialMedia.dto.feed.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.member.Member;

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

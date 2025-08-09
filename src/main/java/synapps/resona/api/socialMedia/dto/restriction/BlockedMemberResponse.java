package synapps.resona.api.socialMedia.dto.restriction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.entity.restriction.Block;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class BlockedMemberResponse {

  private Long blockId;
  private Long memberId;
  private String nickname;
  private String profileImageUrl;

  public static BlockedMemberResponse from(Block block) {
    return BlockedMemberResponse.of(
        block.getId(),
        block.getBlocked().getId(),
        block.getBlocked().getProfile().getNickname(),
        block.getBlocked().getProfile().getProfileImageUrl()
    );
  }
}
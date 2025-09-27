package com.synapps.resona.restriction.dto;

import com.synapps.resona.restriction.entity.Block;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
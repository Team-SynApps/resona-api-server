package com.synapps.resona.dto.response;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.entity.profile.Profile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class MemberProfileDto {

  private Long memberId;
  private String profile_image_url;
  private String nickname;
  private String tag;


  public static MemberProfileDto from(Member member, Profile profile) {
    return MemberProfileDto.of(member.getId(), profile.getProfileImageUrl(), profile.getNickname(),
        profile.getTag());
  }
}
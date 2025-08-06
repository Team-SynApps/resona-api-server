package synapps.resona.api.mysql.member.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.profile.Profile;

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
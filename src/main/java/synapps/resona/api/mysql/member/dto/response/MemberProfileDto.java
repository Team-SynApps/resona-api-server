package synapps.resona.api.mysql.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.profile.Profile;

@Data
@AllArgsConstructor
@Getter
public class MemberProfileDto {
    private Long memberId;
    private String profile_image_url;
    private String nickname;
    private String tag;


    public static MemberProfileDto from(Member member, Profile profile) {
        return new MemberProfileDto(member.getId(), profile.getProfileImageUrl(), profile.getNickname(), profile.getTag());
    }
}
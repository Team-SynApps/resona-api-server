package synapps.resona.api.member.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.oauth.entity.UserPrincipal;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class MemberDto {

  private Long id;
  private String email;

  public static MemberDto from(UserPrincipal userPrincipal) {
    return MemberDto.of(userPrincipal.getMemberId(), userPrincipal.getEmail());
  }
}
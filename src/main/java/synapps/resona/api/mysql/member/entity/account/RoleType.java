package synapps.resona.api.mysql.member.entity.account;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {
  USER("USER", "일반 사용자 권한"),
  ADMIN("ADMIN", "관리자 권한"),
  GUEST("GUEST", "게스트 권한");

  private final String code;
  private final String displayName;

  public static RoleType of(String code) {
    return Arrays.stream(RoleType.values())
        .filter(r -> r.getCode().equals(code))
        .findAny()
        .orElse(GUEST);
  }
}
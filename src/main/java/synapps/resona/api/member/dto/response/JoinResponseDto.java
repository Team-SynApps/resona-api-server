package synapps.resona.api.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원가입 및 로그인 성공 응답 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class JoinResponseDto {

  @Schema(description = "가입된 회원 정보")
  private MemberRegisterResponseDto memberInfo;

  @Schema(description = "발급된 토큰 정보")
  private TokenResponse tokenInfo;
}
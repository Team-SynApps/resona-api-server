package com.synapps.resona.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class JoinResponseDto {

  private MemberRegisterResponseDto memberInfo;

  private TokenResponse tokenInfo;
}
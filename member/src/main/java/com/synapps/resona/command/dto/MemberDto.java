package com.synapps.resona.command.dto;

import com.synapps.resona.command.entity.member.UserPrincipal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
package com.synapps.resona.command.dto.request.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class MemberPasswordChangeDto {

  private String email;
  private String changedPassword;
}

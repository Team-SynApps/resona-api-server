package com.synapps.resona.command.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMemberDto {

  private String memberEmail;
  private Boolean isMember;
}

package com.synapps.resona.mysql.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMemberDto {
    private String memberEmail;
    private Boolean isMember;
}

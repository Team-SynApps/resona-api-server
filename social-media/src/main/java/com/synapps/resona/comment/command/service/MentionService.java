package com.synapps.resona.comment.command.service;

import com.synapps.resona.comment.query.entity.MentionedMember;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.service.MemberService;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MentionService {
  private final MemberService memberService;

  /**
   * 언급된 사용자 ID 목록을 받아 MentionedMember DTO 목록으로 변환
   * @param memberIds 언급된 사용자 ID 리스트
   * @return MentionedMember DTO 리스트
   */
  public List<MentionedMember> parseMentions(List<Long> memberIds) {
    if (memberIds == null || memberIds.isEmpty()) {
      return Collections.emptyList();
    }

    return memberIds.stream()
        .map(memberId -> {
          Member member = memberService.getMemberWithProfile(memberId);
          String nickname = member.getProfile().getNickname();
          return MentionedMember.of(memberId, nickname);
        })
        .collect(Collectors.toList());
  }
}

package com.synapps.resona.report.command.service;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.service.MemberService;
import com.synapps.resona.report.dto.request.SanctionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SanctionService {

    private final MemberService memberService;

    @Transactional
    public void sanctionUser(SanctionRequest request) {
        Member member = memberService.getMember(request.getMemberId());
        int sanctionDays = request.getSanctionDays();

        if (sanctionDays == -1) {
            member.getAccountInfo().ban(LocalDateTime.now().plusYears(100));
        } else {
            member.getAccountInfo().ban(LocalDateTime.now().plusDays(sanctionDays));
        }
    }
}

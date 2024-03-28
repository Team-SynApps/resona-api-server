package com.synapps.atch.mysql.member.service;

import com.synapps.atch.mysql.member.entity.Member;
import com.synapps.atch.mysql.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member getMember() {
        User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return memberRepository.findByEmail(userPrincipal.getUsername());
    }

}

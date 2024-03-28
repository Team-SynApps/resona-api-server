package com.synapps.atch.mysql.member.service;

import com.synapps.atch.mysql.member.entity.Member;
import com.synapps.atch.mysql.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    /**
     * SecurityContextHolder에서 관리하는 context에서 userPrincipal을 받아옴
     * @return 멤버를 이메일 기준으로 불러옴
     */
    public Member getMember() {
        User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return memberRepository.findByEmail(userPrincipal.getUsername());
    }

}

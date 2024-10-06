package com.synapps.resona.oauth.service;

import com.synapps.resona.mysql.member.entity.Member;
import com.synapps.resona.mysql.member.exception.MemberException;
import com.synapps.resona.mysql.member.repository.MemberRepository;
import com.synapps.resona.oauth.entity.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberException::memberNotFound);
        return UserPrincipal.create(member);
    }
}

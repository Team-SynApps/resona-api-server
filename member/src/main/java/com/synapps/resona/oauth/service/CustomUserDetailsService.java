package com.synapps.resona.oauth.service;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.entity.member.UserPrincipal;
import com.synapps.resona.exception.MemberException;
import com.synapps.resona.command.repository.member.MemberRepository;
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
    Member member = memberRepository.findWithAccountInfoByEmail(email)
        .orElseThrow(MemberException::memberNotFound);

    return UserPrincipal.create(member);
  }
}

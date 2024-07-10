package com.synapps.atch.mysql.member.service;

import com.synapps.atch.mysql.member.dto.request.DuplicateIdRequest;
import com.synapps.atch.mysql.member.dto.request.SignupRequest;
import com.synapps.atch.mysql.member.entity.Member;
import com.synapps.atch.mysql.member.entity.Sex;
import com.synapps.atch.mysql.member.repository.MemberRepository;
import com.synapps.atch.oauth.entity.ProviderType;
import com.synapps.atch.oauth.entity.RoleType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    /**
     * SecurityContextHolder에서 관리하는 context에서 userPrincipal을 받아옴
     * @return 멤버를 이메일 기준으로 불러옴
     */
    public Member getMember() {
        System.out.println("get member");
        User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return memberRepository.findByEmail(userPrincipal.getUsername());
    }

    @Transactional
    public Member signUp(SignupRequest request) throws Exception {
        if (!request.getCode().equals("code")) {
            throw new Exception("코드가 일치하지 않습니다");
        }
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new Exception("이미 존재하는 이메일입니다.");
        }

        Member member = Member.of(
                request.getNickname(),
                request.getPhoneNumber(),
                request.getTimezone(),
                request.getAge(),
                request.getBirth(),
                request.getComment(),
                Sex.of(request.getSex()),
                false, // isOnline 초기값
                request.getEmail(),
                request.getPassword(),
                request.getLocation(),
                ProviderType.of(request.getProviderType()),
                RoleType.USER,
                LocalDateTime.now(), // createdAt
                LocalDateTime.now(), // modifiedAt
                LocalDateTime.now()  // lastAccessedAt
        );
        member.encodePassword(request.getPassword());
        memberRepository.save(member);
        return member;
    }

    public boolean checkDuplicateId(DuplicateIdRequest request) throws Exception {
        return memberRepository.existsById(Long.parseLong(request.getUserId()));
    }

    public String deleteUser() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByEmail(principal.getUsername());
        memberRepository.delete(member);
        return "delete successful";
    }

}

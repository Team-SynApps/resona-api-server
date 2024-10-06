package com.synapps.resona.mysql.member.service;

import com.synapps.resona.global.utils.DateTimeUtil;
import com.synapps.resona.mysql.member.dto.request.DuplicateIdRequest;
import com.synapps.resona.mysql.member.dto.request.SignupRequest;
import com.synapps.resona.mysql.member.dto.response.MemberDto;
import com.synapps.resona.mysql.member.entity.Member;
import com.synapps.resona.mysql.member.entity.Sex;
import com.synapps.resona.mysql.member.exception.MemberException;
import com.synapps.resona.mysql.member.repository.MemberRepository;
import com.synapps.resona.oauth.entity.ProviderType;
import com.synapps.resona.oauth.entity.RoleType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    /**
     * SecurityContextHolder에서 관리하는 context에서 userPrincipal을 받아옴
     * @return 멤버를 이메일 기준으로 불러옴
     * Optional 적용 고려
     */
    public Member getMember() {
        log.info("get member");
        User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info(userPrincipal.getUsername());
        return memberRepository.findByEmail(userPrincipal.getUsername())
                .orElseThrow(MemberException::memberNotFound);
    }

    @Transactional
    public MemberDto signUp(SignupRequest request) throws Exception {
        // code 부분 다른 방식을 적용할 예정 - 삭제해야 함
        if (!request.getCode().equals("code")) {
            throw new Exception("코드가 일치하지 않습니다");
        }
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw MemberException.duplicateEmail();
        }

        Member member = Member.of(
                request.getNickname(),
                request.getPhoneNumber(),
                request.getTimezone(),
                DateTimeUtil.stringToLocalDateTime(request.getBirth()),
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
        MemberDto memberDto = MemberDto.from(member);
        return memberDto;
    }

    public boolean checkDuplicateId(DuplicateIdRequest request) throws Exception {
        return memberRepository.existsById(Long.parseLong(request.getId()));
    }

    public String deleteUser() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByEmail(principal.getUsername()).orElseThrow(MemberException::memberNotFound);
        memberRepository.delete(member);
        return "delete successful";
    }

}

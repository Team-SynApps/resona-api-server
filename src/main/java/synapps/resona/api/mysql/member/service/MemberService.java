package synapps.resona.api.mysql.member.service;

import synapps.resona.api.mysql.member.dto.request.auth.DuplicateIdRequest;
import synapps.resona.api.mysql.member.dto.request.auth.SignupRequest;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.account.AccountStatus;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.repository.AccountInfoRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.RoleType;
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
    private final AccountInfoRepository accountInfoRepository;

    /**
     * SecurityContextHolder에서 관리하는 context에서 userPrincipal을 받아옴
     * @return 멤버를 이메일 기준으로 불러옴
     * Optional 적용 고려
     */
    @Transactional
    public Member getMember() {
        log.info("get member");
        User userPrincipal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info(userPrincipal.getUsername());
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(MemberException::memberNotFound);
        AccountInfo accountInfo = accountInfoRepository.findByMember(member);
        accountInfo.updateLastAccessedAt();
        return member;
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
                request.getEmail(),
                request.getPassword(),
                LocalDateTime.now(), // createdAt
                LocalDateTime.now() // modifiedAt
        );

        AccountInfo accountInfo = AccountInfo.of(
                member,
                RoleType.USER,
                ProviderType.LOCAL,
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        member.encodePassword(request.getPassword());
        memberRepository.save(member);
        accountInfoRepository.save(accountInfo);
        return new MemberDto(member.getId(), member.getEmail());
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

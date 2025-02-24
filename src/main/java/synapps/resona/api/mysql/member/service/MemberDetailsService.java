package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.mysql.member.dto.request.member_details.MemberDetailsRequest;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.repository.MemberDetailsRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberDetailsService {

    private final MemberDetailsRepository memberDetailsRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;


    /**
     * PersonalInfo 등록
     */
    @Transactional
    public MemberDetails register(MemberDetailsRequest request) {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId())
                .orElseThrow(MemberException::memberNotFound);

        MemberDetails memberDetails = MemberDetails.of(
                member,
                request.getTimezone(),
                request.getPhoneNumber(),
                null,  // MBTI는 null로 설정
                "",    // aboutMe는 빈 문자열로 설정
                request.getLocation()
        );

        return memberDetailsRepository.save(memberDetails);
    }

    /**
     * PersonalInfo 조회
     */
    public MemberDetails getMemberDetails() {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId())
                .orElseThrow(MemberException::memberNotFound);

        return memberDetailsRepository.findByMember(member)
                .orElseThrow(MemberException::memberNotFound);
    }

    public MemberDetails getMemberDetailsByMemberId(Long memberId) {
        return memberDetailsRepository.findByMemberId(memberId).orElseThrow(MemberException::memberNotFound);
    }

    /**
     * PersonalInfo 수정
     */
    @Transactional
    public MemberDetails editMemberDetails(MemberDetailsRequest request) {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId())
                .orElseThrow(MemberException::memberNotFound);

        MemberDetails memberDetails = memberDetailsRepository.findByMember(member)
                .orElseThrow(MemberException::memberNotFound);

        memberDetails.updatePersonalInfo(
                request.getTimezone(),
                request.getPhoneNumber(),
                null,  // MBTI는 null로 유지
                "",    // aboutMe는 빈 문자열로 유지
                request.getLocation()
        );

        return memberDetails;
    }

    /**
     * PersonalInfo 삭제
     */
    @Transactional
    public MemberDetails deleteMemberDetails() {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId())
                .orElseThrow(MemberException::memberNotFound);

        MemberDetails memberDetails = memberDetailsRepository.findByMember(member)
                .orElseThrow(MemberException::memberNotFound);

        memberDetails.softDelete();
        return memberDetails;
    }
}


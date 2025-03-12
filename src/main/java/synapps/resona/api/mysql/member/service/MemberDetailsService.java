package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.mysql.member.dto.request.member_details.MemberDetailsRequest;
import synapps.resona.api.mysql.member.dto.response.MemberDetailsDto;
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
     * TODO: 이미 존재하는 멤버 정보인 경우, 오류 처리 필요
     */
    @Transactional
    public MemberDetailsDto register(MemberDetailsRequest request) {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId())
                .orElseThrow(MemberException::memberNotFound);

        MemberDetails memberDetails = MemberDetails.of(
                member,
                request.getTimezone(),
                request.getPhoneNumber(),
                request.getMbti(),
                request.getAboutMe(),
                request.getLocation()
        );
        MemberDetails memberDetailsResult = memberDetailsRepository.save(memberDetails);

        return MemberDetailsDto.builder()
                .id(memberDetailsResult.getId())
                .aboutMe(memberDetailsResult.getAboutMe())
                .location(memberDetailsResult.getLocation())
                .mbti(memberDetailsResult.getMbti())
                .memberId(memberDetailsResult.getMember().getId())
                .timezone(memberDetailsResult.getTimezone())
                .phoneNumber(memberDetailsResult.getPhoneNumber()).build();
    }

    /**
     * PersonalInfo 조회
     */
    public MemberDetailsDto getMemberDetails() {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId())
                .orElseThrow(MemberException::memberNotFound);

        MemberDetails memberDetails = memberDetailsRepository.findByMember(member).orElseThrow(MemberException::memberNotFound);


        return MemberDetailsDto.builder()
                .id(memberDetails.getId())
                .aboutMe(memberDetails.getAboutMe())
                .location(memberDetails.getLocation())
                .mbti(memberDetails.getMbti())
                .memberId(memberDetails.getMember().getId())
                .timezone(memberDetails.getTimezone())
                .phoneNumber(memberDetails.getPhoneNumber()).build();
    }

    /**
     * 전화번호는 제외하고 반환
     * @param memberId
     * @return
     */
    public MemberDetailsDto getMemberDetails(Long memberId) {
        MemberDetails memberDetails = memberDetailsRepository.findByMemberId(memberId).orElseThrow(MemberException::memberNotFound);

        return MemberDetailsDto.builder()
                .id(memberDetails.getId())
                .aboutMe(memberDetails.getAboutMe())
                .location(memberDetails.getLocation())
                .mbti(memberDetails.getMbti())
                .memberId(memberDetails.getMember().getId())
                .timezone(memberDetails.getTimezone())
                .build();
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
                request.getMbti(),
                request.getAboutMe(),
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


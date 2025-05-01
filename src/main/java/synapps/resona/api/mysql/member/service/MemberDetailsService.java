package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.mysql.member.dto.request.member_details.MemberDetailsRequest;
import synapps.resona.api.mysql.member.dto.response.MemberDetailsDto;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.exception.MemberDetailsException;
import synapps.resona.api.mysql.member.repository.MemberDetailsRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberDetailsService {

  private final MemberDetailsRepository memberDetailsRepository;
  private final MemberRepository memberRepository;
  private final MemberService memberService;

  private static void changeMemberDetails(MemberDetailsRequest request,
      MemberDetails memberDetails) {
    memberDetails.modifyMemberDetails(
        request.getTimezone(),
        request.getPhoneNumber(),
        request.getMbti(),
        request.getAboutMe(),
        request.getLocation()
    );
  }

  private static MemberDetailsDto buildMemberDetailsDto(MemberDetails memberDetails) {
    return MemberDetailsDto.builder()
        .id(memberDetails.getId())
        .aboutMe(memberDetails.getAboutMe())
        .location(memberDetails.getLocation())
        .phoneNumber(memberDetails.getPhoneNumber())
        .mbti(memberDetails.getMbti())
        .timezone(memberDetails.getTimezone())
        .build();
  }

//    /**
//     * 전화번호는 제외하고 반환
//     * @param memberId
//     * @return
//     */
//    public MemberDetailsDto getMemberDetails(Long memberId) {
//        MemberDetails memberDetails = memberDetailsRepository.findByMemberId(memberId).orElseThrow(MemberException::memberNotFound);
//
//        return buildMemberDetailsDto(memberDetails);
//    }

  @Transactional
  public MemberDetailsDto register(MemberDetailsRequest request) {
    String memberEmail = memberService.getMemberEmail();

    MemberDetails memberDetails = memberRepository.findMemberDetailsByEmail(memberEmail)
        .orElseThrow(MemberDetailsException::memberDetailsNotFound);
    changeMemberDetails(request, memberDetails);

    MemberDetails registeredMemberDetails = memberDetailsRepository.save(memberDetails);

    return buildMemberDetailsDto(registeredMemberDetails);
  }

  public MemberDetailsDto getMemberDetails() {
    String memberEmail = memberService.getMemberEmail();
    MemberDetails memberDetails = memberRepository.findMemberDetailsByEmail(memberEmail)
        .orElseThrow(MemberDetailsException::memberDetailsNotFound);

    return buildMemberDetailsDto(memberDetails);
  }

  @Transactional
  public MemberDetails editMemberDetails(MemberDetailsRequest request) {
    String memberEmail = memberService.getMemberEmail();
    MemberDetails memberDetails = memberRepository.findMemberDetailsByEmail(memberEmail)
        .orElseThrow(MemberDetailsException::memberDetailsNotFound);

    changeMemberDetails(request, memberDetails);

    return memberDetails;
  }

  @Transactional
  public MemberDetails deleteMemberDetails() {
    String memberEmail = memberService.getMemberEmail();

    MemberDetails memberDetails = memberRepository.findMemberDetailsByEmail(memberEmail)
        .orElseThrow(MemberDetailsException::memberDetailsNotFound);
    memberDetails.softDelete();
    return memberDetails;
  }
}


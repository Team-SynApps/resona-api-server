package synapps.resona.api.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.member.dto.request.member_details.MemberDetailsRequest;
import synapps.resona.api.member.dto.response.MemberDetailsResponse;
import synapps.resona.api.member.entity.member_details.MemberDetails;
import synapps.resona.api.member.exception.MemberDetailsException;
import synapps.resona.api.member.repository.member_details.MemberDetailsRepository;
import synapps.resona.api.member.repository.member.MemberRepository;

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

  @Transactional
  public MemberDetailsResponse register(MemberDetailsRequest request) {
    String memberEmail = memberService.getMemberEmail();

    MemberDetails memberDetails = memberRepository.findMemberDetailsByEmail(memberEmail)
        .orElseThrow(MemberDetailsException::memberDetailsNotFound);
    changeMemberDetails(request, memberDetails);

    MemberDetails registeredMemberDetails = memberDetailsRepository.save(memberDetails);

    return MemberDetailsResponse.from(registeredMemberDetails);
  }

  public MemberDetailsResponse getMemberDetails() {
    String memberEmail = memberService.getMemberEmail();
    MemberDetails memberDetails = memberRepository.findMemberDetailsByEmail(memberEmail)
        .orElseThrow(MemberDetailsException::memberDetailsNotFound);

    return MemberDetailsResponse.from(memberDetails);
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


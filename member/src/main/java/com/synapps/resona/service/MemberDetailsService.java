package com.synapps.resona.service;

import com.synapps.resona.dto.request.member_details.MemberDetailsRequest;
import com.synapps.resona.dto.response.MemberDetailsResponse;
import com.synapps.resona.entity.member_details.MemberDetails;
import com.synapps.resona.exception.MemberDetailsException;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.repository.member_details.MemberDetailsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


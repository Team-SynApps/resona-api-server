package com.synapps.resona.command.service;

import com.synapps.resona.command.dto.request.member_details.MemberDetailsRequest;
import com.synapps.resona.command.dto.response.MemberDetailsResponse;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.entity.member_details.MemberDetails;
import com.synapps.resona.command.event.MemberDetailsUpdatedEvent;
import com.synapps.resona.exception.MemberDetailsException;
import com.synapps.resona.command.repository.member.MemberRepository;
import com.synapps.resona.command.repository.member_details.MemberDetailsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailsService {

  private final MemberDetailsRepository memberDetailsRepository;
  private final MemberRepository memberRepository;
  private final MemberService memberService;
  private final ApplicationEventPublisher eventPublisher;

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
    Member member = memberService.getMemberUsingSecurityContext();

    MemberDetails memberDetails = member.getMemberDetails();
    if (memberDetails == null) {
        throw MemberDetailsException.memberDetailsNotFound();
    }
    changeMemberDetails(request, memberDetails);

    MemberDetails registeredMemberDetails = memberDetailsRepository.save(memberDetails);

    publishMemberDetailsUpdatedEvent(member.getId(), registeredMemberDetails);

    return MemberDetailsResponse.from(registeredMemberDetails);
  }


  @Transactional
  public MemberDetails editMemberDetails(MemberDetailsRequest request) {
    Member member = memberService.getMemberUsingSecurityContext();
    MemberDetails memberDetails = member.getMemberDetails();
    if (memberDetails == null) {
        throw MemberDetailsException.memberDetailsNotFound();
    }

    changeMemberDetails(request, memberDetails);
    publishMemberDetailsUpdatedEvent(member.getId(), memberDetails);

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

  private void publishMemberDetailsUpdatedEvent(Long memberId, MemberDetails memberDetails) {
    MemberDetailsUpdatedEvent event = new MemberDetailsUpdatedEvent(
        memberId,
        memberDetails.getTimezone(),
        memberDetails.getPhoneNumber(),
        memberDetails.getMbti(),
        memberDetails.getAboutMe(),
        memberDetails.getLocation()
    );
    eventPublisher.publishEvent(event);
  }
}

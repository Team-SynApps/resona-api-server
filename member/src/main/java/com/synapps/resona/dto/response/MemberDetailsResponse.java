package com.synapps.resona.dto.response;

import com.synapps.resona.entity.member_details.MBTI;
import com.synapps.resona.entity.member_details.MemberDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDetailsResponse {

  private Long id;
  private Integer timezone;
  private String phoneNumber;
  private MBTI mbti;
  private String aboutMe;
  private String location;

  public static MemberDetailsResponse from(MemberDetails memberDetails) {
    return MemberDetailsResponse.builder()
        .id(memberDetails.getId())
        .aboutMe(memberDetails.getAboutMe())
        .location(memberDetails.getLocation())
        .phoneNumber(memberDetails.getPhoneNumber())
        .mbti(memberDetails.getMbti())
        .timezone(memberDetails.getTimezone())
        .build();
  }
}
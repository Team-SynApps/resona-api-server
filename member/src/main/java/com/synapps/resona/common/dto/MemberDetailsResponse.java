package com.synapps.resona.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.synapps.resona.command.entity.hobby.Hobby;
import com.synapps.resona.command.entity.member_details.MBTI;
import com.synapps.resona.command.entity.member_details.MemberDetails;
import com.synapps.resona.query.entity.MemberDocument;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberDetailsResponse {

  private Long id;
  private Integer timezone;
  private String phoneNumber;
  private MBTI mbti;
  private String aboutMe;
  private String location;
  private List<String> hobbies;

  public static MemberDetailsResponse from(MemberDetails memberDetails, Set<Hobby> hobbies) {
    List<String> hobbyNames = new ArrayList<>();
    hobbies.forEach(hobby -> {
      hobbyNames.add(hobby.getName());
    });

    return MemberDetailsResponse.builder()
        .id(memberDetails.getId())
        .aboutMe(memberDetails.getAboutMe())
        .location(memberDetails.getLocation())
        .phoneNumber(memberDetails.getPhoneNumber())
        .mbti(memberDetails.getMbti())
        .timezone(memberDetails.getTimezone())
        .hobbies(hobbyNames)
        .build();
  }

  public static MemberDetailsResponse from(MemberDocument.MemberDetailsEmbed embed) {
    return MemberDetailsResponse.builder()
        .aboutMe(embed.getAboutMe())
        .location(embed.getLocation())
        .phoneNumber(embed.getPhoneNumber())
        .mbti(embed.getMbti())
        .timezone(embed.getTimezone())
        .hobbies(embed.getHobbies())
        .build();
  }
}
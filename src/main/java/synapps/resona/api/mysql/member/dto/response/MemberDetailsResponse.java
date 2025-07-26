package synapps.resona.api.mysql.member.dto.response;

import lombok.Builder;
import lombok.Data;
import synapps.resona.api.mysql.member.entity.member_details.MBTI;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;

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
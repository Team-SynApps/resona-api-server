package synapps.resona.api.mysql.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import synapps.resona.api.mysql.member.entity.member_details.MBTI;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;

@Data
@Builder
public class MemberDetailsDto {

  private Long id;
  private Integer timezone;
  private String phoneNumber;
  private MBTI mbti;
  private String aboutMe;
  private String location;

  public static MemberDetailsDto from(MemberDetails memberDetails) {
    return MemberDetailsDto.builder()
        .id(memberDetails.getId())
        .aboutMe(memberDetails.getAboutMe())
        .location(memberDetails.getLocation())
        .phoneNumber(memberDetails.getPhoneNumber())
        .mbti(memberDetails.getMbti())
        .timezone(memberDetails.getTimezone())
        .build();
  }
}
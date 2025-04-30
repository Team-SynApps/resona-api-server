package synapps.resona.api.mysql.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import synapps.resona.api.mysql.member.entity.member_details.MBTI;

@Data
@AllArgsConstructor
@Builder
public class MemberDetailsDto {
    private Long id;
    private Integer timezone;
    private String phoneNumber;
    private MBTI mbti;
    private String aboutMe;
    private String location;
}
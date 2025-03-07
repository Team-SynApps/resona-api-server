package synapps.resona.api.mysql.member.dto.request.member_details;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.member_details.MBTI;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetailsRequest {

    @NotNull
    private Long memberId;

    private Integer timezone;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    private MBTI mbti;

    private String aboutMe;

    private String location;
}
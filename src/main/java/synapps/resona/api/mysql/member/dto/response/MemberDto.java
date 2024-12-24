package synapps.resona.api.mysql.member.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class MemberDto {
    private Long id;
    private String email;
}
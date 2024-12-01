package synapps.resona.api.mysql.member.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String email;
}
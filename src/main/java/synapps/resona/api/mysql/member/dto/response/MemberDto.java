package synapps.resona.api.mysql.member.dto.response;

import lombok.*;
import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.mysql.member.entity.Member;
import synapps.resona.api.mysql.member.entity.personal_info.Gender;
import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.RoleType;

@Data
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String email;
}
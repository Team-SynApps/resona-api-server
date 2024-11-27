package synapps.resona.api.mysql.member.dto.request.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DuplicateIdRequest {
    private String id;
}

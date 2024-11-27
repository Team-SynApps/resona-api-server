package synapps.resona.api.mysql.member.dto.request.interests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestsRequest {
    private Long memberId;
    private List<String> interestedLanguages;
    private List<String> hobbies;
}

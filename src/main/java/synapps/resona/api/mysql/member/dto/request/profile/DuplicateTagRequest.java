package synapps.resona.api.mysql.member.dto.request.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DuplicateTagRequest {

  private String tag;
}

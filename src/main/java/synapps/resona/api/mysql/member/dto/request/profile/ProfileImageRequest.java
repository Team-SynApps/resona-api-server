package synapps.resona.api.mysql.member.dto.request.profile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ProfileImageRequest {

  @NotNull
  private Long memberId; // Member와 매핑할 ID

  @Size(max = 512)
  private String imageUrl;
}

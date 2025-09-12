package synapps.resona.api.socialMedia.media.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedImageUpdateRequest {

  private Long FeedImageId;
  private String url;
}

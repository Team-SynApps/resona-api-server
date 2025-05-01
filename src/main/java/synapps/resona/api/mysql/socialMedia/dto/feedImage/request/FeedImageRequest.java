package synapps.resona.api.mysql.socialMedia.dto.feedImage.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedImageRequest {

  private Long feedId;
  private String url;
  private String type;
  private int index;
}

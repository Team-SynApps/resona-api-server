package synapps.resona.api.mysql.socialMedia.dto.feed.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.dto.feedImage.FeedImageDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedResponse {

  private String id;
  private String content;
  private List<FeedImageDto> feedImageDtos;
  private String createdAt;
}

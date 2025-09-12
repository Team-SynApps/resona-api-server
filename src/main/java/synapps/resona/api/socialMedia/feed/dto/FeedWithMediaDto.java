package synapps.resona.api.socialMedia.feed.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.media.dto.FeedMediaDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedWithMediaDto {

  private Long feedId;
  private String content;
  private long likeCount;
  private List<FeedMediaDto> images;
}

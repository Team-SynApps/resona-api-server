package synapps.resona.api.mysql.socialMedia.dto.feed;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.dto.media.FeedMediaDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedWithMediaDto {

  private Long feedId;
  private String content;
  private int likeCount;
  private List<FeedMediaDto> images;
}

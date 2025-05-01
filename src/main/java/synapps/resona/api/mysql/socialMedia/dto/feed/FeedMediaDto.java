package synapps.resona.api.mysql.socialMedia.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.socialMedia.entity.feedMedia.FeedMedia;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedMediaDto {

  private Long id;
  private String url;

  public static FeedMediaDto from(FeedMedia media) {
    return new FeedMediaDto(media.getId(), media.getUrl());
  }
}

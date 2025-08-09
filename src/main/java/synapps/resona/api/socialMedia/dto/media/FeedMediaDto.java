package synapps.resona.api.socialMedia.dto.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.entity.media.FeedMedia;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class FeedMediaDto {

  private Long id;
  private String url;

  public static FeedMediaDto from(FeedMedia media) {
    return FeedMediaDto.of(media.getId(), media.getUrl());
  }
}

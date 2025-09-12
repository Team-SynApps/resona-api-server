package synapps.resona.api.socialMedia.media.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import synapps.resona.api.socialMedia.media.entity.FeedMedia;

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

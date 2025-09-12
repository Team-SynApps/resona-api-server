package synapps.resona.api.socialMedia.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import synapps.resona.api.socialMedia.feed.entity.Feed;

@Getter
@AllArgsConstructor
public class FeedDetailDto {
  private Feed feed;
  private FeedMetaData metaData;


  public static FeedDetailDto of(Feed feed, FeedMetaData feedMetaData) {
    return new FeedDetailDto(feed, feedMetaData);
  }
}

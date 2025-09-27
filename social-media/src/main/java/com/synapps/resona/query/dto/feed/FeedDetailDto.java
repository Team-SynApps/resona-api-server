package com.synapps.resona.query.dto.feed;

import com.synapps.resona.domain.entity.feed.Feed;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedDetailDto {
  private Feed feed;
  private FeedMetaData metaData;


  public static FeedDetailDto of(Feed feed, FeedMetaData feedMetaData) {
    return new FeedDetailDto(feed, feedMetaData);
  }
}

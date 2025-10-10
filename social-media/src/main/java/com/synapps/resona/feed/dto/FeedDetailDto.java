package com.synapps.resona.feed.dto;

import com.synapps.resona.feed.command.entity.Feed;
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

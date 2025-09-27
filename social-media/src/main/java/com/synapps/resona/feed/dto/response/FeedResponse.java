package com.synapps.resona.feed.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import com.synapps.resona.media.dto.FeedImageDto;
import com.synapps.resona.feed.entity.Feed;

@Data
@Builder
public class FeedResponse {

  private String id;
  private String content;
  private List<FeedImageDto> feedImageDtos;
  private String createdAt;

  public static FeedResponse from(Feed feed, List<FeedImageDto> feedImageDtos) {
    return FeedResponse.builder()
        .id(feed.getId().toString())
        .feedImageDtos(feedImageDtos)
        .createdAt(feed.getCreatedAt().toString())
        .content(feed.getContent()).build();
  }

  public static FeedResponse from(Feed feed) {
    return FeedResponse.builder()
        .id(feed.getId().toString())
        .content(feed.getContent())
        .createdAt(feed.getCreatedAt().toString())
        .feedImageDtos(feed.getImages().stream().map(FeedImageDto::from).toList())
        .build();
  }
}

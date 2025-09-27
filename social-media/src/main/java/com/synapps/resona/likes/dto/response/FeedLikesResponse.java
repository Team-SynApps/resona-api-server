package com.synapps.resona.likes.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FeedLikesResponse extends LikesResponse {
  private final Long feedId;

  public static FeedLikesResponse of(Long feedId, long likesCount, boolean isLiked) {
    return FeedLikesResponse.builder()
        .feedId(feedId)
        .likesCount(likesCount)
        .isLiked(isLiked)
        .build();
  }
}
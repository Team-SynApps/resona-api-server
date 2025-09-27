package com.synapps.resona.query.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedMetaData {
  private long likeCount;
  private long commentCount;

  private boolean hasLiked;
  private boolean hasScraped;
}

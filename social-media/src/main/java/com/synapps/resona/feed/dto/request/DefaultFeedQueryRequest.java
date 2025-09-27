package com.synapps.resona.feed.dto.request;

import lombok.Data;

@Data
public class DefaultFeedQueryRequest implements FeedQueryRequest {
  Long memberId;
}

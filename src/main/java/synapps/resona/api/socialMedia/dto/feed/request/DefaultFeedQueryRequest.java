package synapps.resona.api.socialMedia.dto.feed.request;

import lombok.Data;

@Data
public class DefaultFeedQueryRequest implements FeedQueryRequest {
  Long memberId;
}

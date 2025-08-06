package synapps.resona.api.mysql.socialMedia.dto.feed.request;

import lombok.Data;

@Data
public class DefaultFeedQueryRequest implements FeedQueryRequest {
  Long memberId;
}

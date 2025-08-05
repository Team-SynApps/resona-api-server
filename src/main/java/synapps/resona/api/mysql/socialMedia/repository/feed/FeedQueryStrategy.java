package synapps.resona.api.mysql.socialMedia.repository.feed;

import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.mysql.socialMedia.dto.feed.FeedDto;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedQueryRequest;

public interface FeedQueryStrategy<T extends FeedQueryRequest> {
  CursorResult<FeedDto> findFeeds(T request);

  boolean supports(Class<? extends FeedQueryRequest> requestClass);
}

package synapps.resona.api.socialMedia.repository.feed;

import synapps.resona.api.socialMedia.dto.feed.FeedDetailDto;

public interface FeedRepositoryCustom {

  FeedDetailDto findFeedDetailById(Long feedId, Long viewerId);
}

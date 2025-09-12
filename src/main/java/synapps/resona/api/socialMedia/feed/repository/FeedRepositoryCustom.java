package synapps.resona.api.socialMedia.feed.repository;

import synapps.resona.api.socialMedia.feed.dto.FeedDetailDto;

public interface FeedRepositoryCustom {

  FeedDetailDto findFeedDetailById(Long feedId, Long viewerId);
}

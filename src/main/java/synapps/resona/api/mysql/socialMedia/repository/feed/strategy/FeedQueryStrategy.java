package synapps.resona.api.mysql.socialMedia.repository.feed.strategy;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import synapps.resona.api.mysql.socialMedia.dto.feed.response.FeedDto;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedQueryRequest;

public interface FeedQueryStrategy<T extends FeedQueryRequest> {
  List<FeedDto> findFeeds(T request, LocalDateTime cursor, Pageable pageable);

  boolean supports(Class<? extends FeedQueryRequest> requestClass);
}

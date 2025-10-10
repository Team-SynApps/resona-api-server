package com.synapps.resona.retrieval.service.strategy;

import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecentCategoryFallbackStrategy implements FallbackStrategy {

  private final FeedReadRepository feedReadRepository;

  @Override
  public boolean supports(FallbackStrategyContext context) {
    return context.countryOfResidence() == null && context.category() != null;
  }

  @Override
  public List<FeedDocument> findFeeds(FallbackStrategyContext context) {
    return feedReadRepository.findByCategoryOrderByCreatedAtDesc(context.category(), context.pageable());
  }
}
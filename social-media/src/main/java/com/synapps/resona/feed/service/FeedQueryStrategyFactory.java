package com.synapps.resona.feed.service;

import com.synapps.resona.feed.dto.request.FeedQueryRequest;
import com.synapps.resona.feed.repository.strategy.FeedQueryStrategy;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FeedQueryStrategyFactory {
  private final Map<Class<? extends FeedQueryRequest>, FeedQueryStrategy<?>> strategyMap;
  private static final Logger logger = LoggerFactory.getLogger(FeedQueryStrategyFactory.class);

  public FeedQueryStrategyFactory(List<FeedQueryStrategy<?>> strategies) {
    this.strategyMap = strategies.stream()
        .collect(Collectors.toUnmodifiableMap(
            // Key: 각 Strategy가 스스로 지원한다고 알려주는 Condition 타입 사용
            FeedQueryStrategy::getSupportedConditionType,
            // Value: Strategy 인스턴스 자신
            Function.identity()
        ));

    logger.info("Initialized FeedQueryStrategyFactory with strategies: {}", strategyMap.keySet());
  }

  @SuppressWarnings("unchecked")
  public <T extends FeedQueryRequest> FeedQueryStrategy<T> findStrategy(Class<T> requestClass) {
    FeedQueryStrategy<?> strategy = strategyMap.get(requestClass);
    if (strategy == null) {
      logger.error("Cannot find supported feed retrieval strategy: {}", requestClass.getSimpleName());
      throw new IllegalArgumentException("Cannot find supported feed retrieval strategy: " + requestClass.getSimpleName());
    }
    return (FeedQueryStrategy<T>) strategy;
  }
}
package synapps.resona.api.mysql.socialMedia.service.feed;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedQueryRequest;
import synapps.resona.api.mysql.socialMedia.repository.feed.strategy.FeedQueryStrategy;

@Component
public class FeedQueryStrategyFactory {
  private final Map<Class<? extends FeedQueryRequest>, FeedQueryStrategy<?>> strategyMap;

  public FeedQueryStrategyFactory(List<FeedQueryStrategy<?>> strategies) {
    this.strategyMap = strategies.stream()
        .collect(Collectors.toUnmodifiableMap(
            strategy -> {
              // supports 메서드를 통해 어떤 DTO 클래스를 지원하는지 확인
              if (strategy.supports(synapps.resona.api.mysql.socialMedia.dto.feed.DefaultFeedSearchCondition.class)) {
                return synapps.resona.api.mysql.socialMedia.dto.feed.DefaultFeedSearchCondition.class;
              }
              throw new IllegalStateException("Unsupported strategy implementation: " + strategy.getClass());
            },
            Function.identity()
        ));
  }

  @SuppressWarnings("unchecked")
  public <T extends FeedQueryRequest> FeedQueryStrategy<T> findStrategy(Class<T> requestClass) {
    FeedQueryStrategy<?> strategy = strategyMap.get(requestClass);
    if (strategy == null) {
      throw new IllegalArgumentException("지원하는 피드 조회 전략을 찾을 수 없습니다: " + requestClass.getSimpleName());
    }
    return (FeedQueryStrategy<T>) strategy;
  }
}
package com.synapps.resona.feed.repository.strategy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import com.synapps.resona.feed.dto.FeedDto;
import com.synapps.resona.feed.dto.request.FeedQueryRequest;

public interface FeedQueryStrategy<T extends FeedQueryRequest> {
  List<FeedDto> findFeeds(T request, LocalDateTime cursor, Pageable pageable, Long viewerId);

  boolean supports(Class<? extends FeedQueryRequest> requestClass);

  /**
   * 제네릭 타입을 통해 자신이 지원하는 Condition 클래스를 반환하는 default 메서드
   * @return 이 전략이 처리하는 FeedQueryRequest 클래스 타입
   */
  @SuppressWarnings("unchecked")
  default Class<T> getSupportedConditionType() {
    Type genericInterface = this.getClass().getGenericInterfaces()[0];
    if (genericInterface instanceof ParameterizedType) {
      return (Class<T>) ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
    }
    LoggerFactory.getLogger(FeedQueryStrategy.class).error("Could not determine the generic type for FeedQueryStrategy.");
    throw new IllegalStateException("Could not determine the generic type for FeedQueryStrategy.");
  }
}

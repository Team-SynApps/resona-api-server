package com.synapps.resona.matching.strategy;

import com.synapps.resona.matching.dto.MatchResult;
import java.util.List;
import org.springframework.stereotype.Component;

@Component("randomMatching")
public class RandomMatchingStrategy implements MatchingStrategy {

  @Override
  public MatchResult match(Long requesterId) {
    // TODO: 랜덤 매칭 기능 구현
    System.out.println("랜덤 매칭 전략 실행...");

    // 임시 id
    Long partnerId = 99L;

    if (partnerId != null) {
      return MatchResult.success(List.of(requesterId, partnerId));
    }
    return MatchResult.failure();
  }
}
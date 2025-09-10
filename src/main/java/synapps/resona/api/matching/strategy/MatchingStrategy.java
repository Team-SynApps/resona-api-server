package synapps.resona.api.matching.strategy;

import synapps.resona.api.matching.dto.MatchResult;

public interface MatchingStrategy {
  MatchResult match(Long requesterId);
}
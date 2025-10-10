package com.synapps.resona.matching.strategy;


import com.synapps.resona.matching.dto.MatchResult;

public interface MatchingStrategy {
  MatchResult match(Long requesterId);
}
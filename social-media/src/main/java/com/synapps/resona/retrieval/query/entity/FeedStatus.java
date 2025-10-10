package com.synapps.resona.retrieval.query.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedStatus {
  ACTIVE("Active"),
  DELETED("Feed deleted"),
  HIDDEN("Feed hidden"),
  BLOCKED("Feed Blocked");

  private final String description;
}
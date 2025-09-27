package com.synapps.resona.domain.entity.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentDisplayStatus {

  NORMAL("Normal"),
  DELETED("Content deleted."),
  HIDDEN("Content hidden"),
  BLOCKED("Content Blocked");

  private final String description;
}

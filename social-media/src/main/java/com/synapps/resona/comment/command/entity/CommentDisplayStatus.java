package com.synapps.resona.comment.command.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentDisplayStatus {

  NORMAL("Normal"),
  DELETED("Content deleted."),
  HIDDEN("Content hidden"),
  BLOCKED("Content Blocked");

  private final String description;
}

package com.synapps.resona.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

  TEXT("text"),
  IMAGE("image"),
  FILE("file"),
  VIDEO("video"),
  SYSTEM("system");

  private final String value;
}
package com.synapps.resona.feed.command.entity;

import java.util.Arrays;

public enum FeedCategory {
  DAILY, QUESTION, TRAVEL, ETC;

  public static FeedCategory of(String s) {
    return Arrays.stream(FeedCategory.values())
        .filter(r -> r.toString().equals(s))
        .findAny().orElse(ETC);
  }
}

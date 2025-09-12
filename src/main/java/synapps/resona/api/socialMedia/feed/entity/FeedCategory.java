package synapps.resona.api.socialMedia.feed.entity;

import java.util.Arrays;

public enum FeedCategory {
  DAILY, QUESTION, POPULAR, NULL;

  public static FeedCategory of(String s) {
    return Arrays.stream(FeedCategory.values())
        .filter(r -> r.toString().equals(s))
        .findAny().orElse(NULL);
  }
}

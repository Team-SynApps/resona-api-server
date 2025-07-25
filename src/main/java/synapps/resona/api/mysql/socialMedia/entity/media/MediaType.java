package synapps.resona.api.mysql.socialMedia.entity.media;

import java.util.Arrays;

public enum MediaType {
  IMAGE, VIDEO, OTHER;

  public static MediaType of(String s) {
    return Arrays.stream(MediaType.values())
        .filter(m -> m.toString().equals(s))
        .findAny().orElse(OTHER);
  }
}

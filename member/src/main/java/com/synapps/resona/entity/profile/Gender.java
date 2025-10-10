package com.synapps.resona.entity.profile;

import java.util.Arrays;

public enum Gender {
  MAN, WOMAN, OTHER, NOT_DECIDED;

  public static Gender of(String sex) {
    return Arrays.stream(Gender.values())
        .filter(r -> r.toString().equals(sex))
        .findAny()
        .orElse(OTHER);
  }
}

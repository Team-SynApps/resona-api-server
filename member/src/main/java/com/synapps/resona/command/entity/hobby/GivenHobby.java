package com.synapps.resona.command.entity.hobby;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum GivenHobby {
  SWIMMING(0, "Swimming"),
  NOT_GIVEN(-1, "not given");

  private final int num;
  private final String name;

  GivenHobby(int num, String name) {
    this.num = num;
    this.name = name;
  }

  public static GivenHobby of(String name) {
    return Arrays.stream(GivenHobby.values())
        .filter(r -> r.toString().equals(name))
        .findAny()
        .orElse(NOT_GIVEN);
  }
}

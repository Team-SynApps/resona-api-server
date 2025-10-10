package com.synapps.resona.report.query.entity;

import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
public class Reported {
  @Indexed
  private Long memberId;
  private final String nickname;

  private Reported(Long memberId, String nickname) {
    this.memberId = memberId;
    this.nickname = nickname;
  }

  public static Reported of(Long memberId, String nickname) {
    return new Reported(memberId, nickname);
  }
}
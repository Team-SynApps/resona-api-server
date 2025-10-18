package com.synapps.resona.report.query.entity;

import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
public class Reporter {
  @Indexed
  private Long memberId;
  private final String nickname;

  private Reporter(Long memberId, String nickname) {
    this.memberId = memberId;
    this.nickname = nickname;
  }

  public static Reporter of(Long memberId, String nickname) {
    return new Reporter(memberId, nickname);
  }
}

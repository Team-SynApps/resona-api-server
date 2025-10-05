package com.synapps.resona.common.entity;

import lombok.Getter;

@Getter
public class Translation {
  private String languageCode;
  private String content;

  private Translation(String language, String content) {
    this.languageCode = language;
    this.content = content;
  }

  public static Translation of(String language, String content) {
    return new Translation(language, content);
  }
}
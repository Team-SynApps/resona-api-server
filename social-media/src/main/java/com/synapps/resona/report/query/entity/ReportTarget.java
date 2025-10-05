package com.synapps.resona.report.query.entity;

import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
public class ReportTarget {
  private final ReportType type;
  @Indexed
  private Long contentId;
  private final String contentSnippet;

  private ReportTarget(ReportType type, Long contentId, String contentSnippet) {
    this.type = type;
    this.contentId = contentId;
    this.contentSnippet = contentSnippet;
  }

  public static ReportTarget of(ReportType type, Long contentId, String contentSnippet) {
    return new ReportTarget(type, contentId, contentSnippet);
  }
}
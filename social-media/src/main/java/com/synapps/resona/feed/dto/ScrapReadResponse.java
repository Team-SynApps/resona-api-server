package com.synapps.resona.feed.dto;

import com.synapps.resona.feed.entity.Scrap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
public class ScrapReadResponse {

  private Long scrapId;
  private Long feedId;
  private String createdAt;

  public static ScrapReadResponse from(Scrap scrap) {
    return ScrapReadResponse.builder()
        .scrapId(scrap.getId())
        .feedId(scrap.getFeed().getId())
        .createdAt(scrap.getCreatedAt().toString())
        .build();
  }
}

package com.synapps.resona.feed.dto;

import com.synapps.resona.feed.command.entity.Scrap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
public class ScrapResponse {

  private Long scrapId;
  private Long feedId;
  private String createdAt;

  public static ScrapResponse from(Scrap scrap) {
    return ScrapResponse.builder()
        .scrapId(scrap.getId())
        .feedId(scrap.getFeed().getId())
        .createdAt(scrap.getCreatedAt().toString())
        .build();
  }
}

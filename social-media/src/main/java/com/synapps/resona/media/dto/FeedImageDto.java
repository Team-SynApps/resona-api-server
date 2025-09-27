package com.synapps.resona.media.dto;

import com.synapps.resona.media.entity.FeedMedia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedImageDto {

  private String url;
  private int index;

  public static FeedImageDto from(FeedMedia media) {
    return FeedImageDto.builder()
        .index(media.getIndex())
        .url(media.getUrl())
        .build();
  }
}
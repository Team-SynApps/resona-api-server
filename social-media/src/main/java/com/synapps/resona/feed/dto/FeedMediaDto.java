package com.synapps.resona.feed.dto;

import com.synapps.resona.feed.command.entity.FeedMedia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class FeedMediaDto {

  private Long id;
  private String url;

  public static FeedMediaDto from(FeedMedia media) {
    return FeedMediaDto.of(media.getId(), media.getUrl());
  }
}

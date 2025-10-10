package com.synapps.resona.feed.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.synapps.resona.feed.dto.ContentDeserializer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedRequest {

  @JsonDeserialize(using = ContentDeserializer.class)
  private String content;
  private String category;
  private LocationRequest location;
  private String languageCode;
}

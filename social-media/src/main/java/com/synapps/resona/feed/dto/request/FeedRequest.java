package com.synapps.resona.feed.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.synapps.resona.feed.dto.ContentDeserializer;
import jakarta.validation.Valid; // @Valid를 import 합니다.
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedRequest {

  @JsonDeserialize(using = ContentDeserializer.class)
  private String content;
  private String category;

  @Valid
  private LocationRequest location;

  private String languageCode;
}
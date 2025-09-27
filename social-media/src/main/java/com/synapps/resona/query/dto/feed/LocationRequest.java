package com.synapps.resona.query.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {

  private String coordinate;
  private String address;
  private String name;
}

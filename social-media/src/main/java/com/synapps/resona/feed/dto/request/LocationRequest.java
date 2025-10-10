package com.synapps.resona.feed.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {

  private String placeId;
  private String displayName;
  private String formattedAddress;
  private GeoLocation location;
  private String primaryType;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GeoLocation {
    private double latitude;
    private double longitude;
  }
}

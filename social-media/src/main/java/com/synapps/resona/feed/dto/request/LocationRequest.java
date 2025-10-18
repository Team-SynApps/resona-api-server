package com.synapps.resona.feed.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequest {

  @NotNull
  private String placeId;
  @NotNull
  private String displayName;
  @NotNull
  private String formattedAddress;
  @Valid
  @NotNull
  private GeoLocation location;
  @NotNull
  private String category;

  private String primaryType;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GeoLocation {
    @NotNull
    private double latitude;
    @NotNull
    private double longitude;
  }
}
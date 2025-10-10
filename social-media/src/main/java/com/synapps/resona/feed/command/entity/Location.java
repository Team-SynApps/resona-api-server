package com.synapps.resona.feed.command.entity;

import com.synapps.resona.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class Location extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "location_id")
  private Long id;

  @Column(name = "place_id")
  private String placeId;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "formatted_address", nullable = false)
  private String formattedAddress;

  @Column(nullable = false)
  private double latitude;

  @Column(nullable = false)
  private double longitude;

  @Column(name = "primary_type")
  private String primaryType;


  private Location(String placeId, String displayName, String formattedAddress, double latitude, double longitude, String primaryType) {
    this.placeId = placeId;
    this.displayName = displayName;
    this.formattedAddress = formattedAddress;
    this.latitude = latitude;
    this.longitude = longitude;
    this.primaryType = primaryType;
  }

  public static Location of(String placeId, String displayName, String formattedAddress, double latitude, double longitude, String primaryType) {
    return new Location(placeId, displayName, formattedAddress, latitude, longitude, primaryType);
  }

}

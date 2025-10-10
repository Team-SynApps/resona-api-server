package com.synapps.resona.feed.event;

import com.synapps.resona.entity.Language;
import com.synapps.resona.entity.profile.CountryCode;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.feed.command.entity.MediaType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record FeedCreatedEvent(
    Long feedId,
    String content,
    FeedCategory category,
    Language language,
    AuthorInfo authorInfo,
    List<MediaInfo> mediaInfos,
    Optional<LocationInfo> locationInfo,
    LocalDateTime createdAt
) {

  public record AuthorInfo(
      Long memberId,
      String nickname,
      String profileImageUrl,
      boolean isCelebrity,
      CountryCode countryOfResidence
  ) {}

  public record MediaInfo(
      MediaType mediaType,
      String url,
      Integer index
  ) {}

  public record LocationInfo(
      String placeId,
      String displayName,
      String formattedAddress,
      GeoLocation location,
      String primaryType
  ) {
    public record GeoLocation(
        double latitude,
        double longitude
    ) {}
  }
}
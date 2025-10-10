package com.synapps.resona.retrieval.service;

import com.synapps.resona.common.entity.Author;
import com.synapps.resona.common.entity.Translation;
import com.synapps.resona.feed.event.FeedCreatedEvent;
import com.synapps.resona.feed.event.FeedLikeChangedEvent;
import com.synapps.resona.feed.event.FeedUpdatedEvent;
import com.synapps.resona.query.member.service.MemberStateService;
import com.synapps.resona.retrieval.port.in.FeedReadModelSyncUseCase;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.entity.FeedDocument.LocationEmbed;
import com.synapps.resona.retrieval.query.entity.FeedDocument.MediaEmbed;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import com.synapps.resona.translation.service.TranslationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedReadModelSyncService implements FeedReadModelSyncUseCase {
  private final FeedReadRepository feedReadRepository;
  private final FeedDocumentUpdateService feedDocumentUpdateService;
  private final MemberStateService memberStateService;
  private final TranslationService translationService;

  @Override
  public void syncCreatedFeed(FeedCreatedEvent event) {
    log.info("FeedCreatedEvent received for feedId: {}", event.feedId());

    // AuthorInfo -> Author
    Author author = Author.of(
        event.authorInfo().memberId(),
        event.authorInfo().nickname(),
        event.authorInfo().profileImageUrl(),
        event.authorInfo().countryOfResidence()
    );

    // MediaInfo list -> MediaEmbed list
    List<MediaEmbed> medias = event.mediaInfos().stream()
        .map(mediaInfo -> MediaEmbed.of(
            mediaInfo.mediaType(),
            mediaInfo.url(),
            mediaInfo.index()
        ))
        .toList();

    // LocationInfo (Optional) -> LocationEmbed
    LocationEmbed location = event.locationInfo()
        .map(locationInfo -> LocationEmbed.of(
            locationInfo.placeId(),
            locationInfo.displayName(),
            locationInfo.formattedAddress(),
            LocationEmbed.GeoLocation.of(
                locationInfo.location().latitude(),
                locationInfo.location().longitude()
            ),
            locationInfo.primaryType()
        ))
        .orElse(null);

    // Translate content
    List<Translation> translations = translationService.translateToTargetLanguages(event.content(), event.language());

    // FeedDocument
    FeedDocument feedDocument = FeedDocument.of(
        event.feedId(),
        author,
        event.content(),
        medias,
        location,
        event.category(),
        event.language(),
        translations
    );

    // save
    feedReadRepository.save(feedDocument);
    log.info("FeedDocument created for feedId: {}", event.feedId());
  }

  @Override
  public void syncUpdatedFeed(FeedUpdatedEvent event) {
    log.info("FeedUpdatedEvent received for feedId: {}", event.feedId());
    feedDocumentUpdateService.updateContent(event.feedId(), event.content());
    log.info("FeedDocument updated for feedId: {}", event.feedId());
  }

  @Override
  public void syncLikedFeed(FeedLikeChangedEvent event) {
    log.info("Feed like changed event received for memberId: {}, feedId: {}, delta: {}",
        event.memberId(), event.feedId(), event.delta());

    if (event.delta() > 0) {
      memberStateService.addLikedFeed(event.memberId(), event.feedId());
    } else {
      memberStateService.removeLikedFeed(event.memberId(), event.feedId());
    }

    feedDocumentUpdateService.updateFeedLikeCount(event.feedId(), event.delta());
  }
}

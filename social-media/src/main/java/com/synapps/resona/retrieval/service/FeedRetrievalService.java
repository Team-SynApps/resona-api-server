package com.synapps.resona.retrieval.service;

import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.comment.query.service.CommentQueryService;
import com.synapps.resona.common.entity.Author;
import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.entity.Language;
import com.synapps.resona.entity.profile.CountryCode;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.feed.event.FeedCreatedEvent;
import com.synapps.resona.feed.event.FeedUpdatedEvent;
import com.synapps.resona.feed.exception.FeedException;
import com.synapps.resona.query.member.entity.MemberStateDocument;
import com.synapps.resona.query.member.service.MemberStateService;
import com.synapps.resona.retrieval.dto.FeedDetailDto;
import com.synapps.resona.retrieval.dto.FeedDto;
import com.synapps.resona.retrieval.port.in.FeedReadModelSyncUseCase;
import com.synapps.resona.retrieval.query.entity.FeedDocument;
import com.synapps.resona.retrieval.query.entity.FeedDocument.LocationEmbed;
import com.synapps.resona.retrieval.query.entity.FeedDocument.MediaEmbed;
import com.synapps.resona.retrieval.query.repository.FeedReadRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedRetrievalService implements FeedReadModelSyncUseCase {

  private final FeedTimelineService feedTimelineService;
  private final MemberFeedService memberFeedService;
  private final FeedReadRepository feedReadRepository;

  private final FeedQueryHelper feedQueryHelper;
  private final MemberStateService memberStateService;
  private final CommentQueryService commentQueryService;

  public CursorResult<FeedDto> getHomeFeeds(Long memberId, Language targetLanguage, String cursor, int size, FeedCategory category) {
    return feedTimelineService.getHomeFeeds(memberId, targetLanguage, cursor, size, category);
  }

  public CursorResult<FeedDto> getExploreFeeds(Long currentMemberId, Language targetLanguage, String cursor, int size, CountryCode residence, FeedCategory category) {
    return feedTimelineService.getExploreFeeds(currentMemberId, targetLanguage, cursor, size, residence, category);
  }

  public Page<FeedDto> getMyFeeds(Long memberId, Language targetLanguage, Pageable pageable) {
    return memberFeedService.getMyFeeds(memberId, targetLanguage, pageable);
  }

  public Page<FeedDto> getMyScrappedFeeds(Long memberId, Language targetLanguage, Pageable pageable) {
    return memberFeedService.getMyScrappedFeeds(memberId, targetLanguage, pageable);
  }

  public FeedDetailDto getFeedDetail(Long feedId, Long currentMemberId, Language targetLanguage, Pageable pageable) {
    FeedDocument feedDocument = feedReadRepository.findByFeedId(feedId)
        .orElseThrow(FeedException::feedNotFoundException);

    FeedDto baseFeedDto = feedQueryHelper.translateAndConvertToDto(feedDocument, targetLanguage);
    Page<CommentDto> comments = commentQueryService.getCommentsForFeed(feedId, currentMemberId, pageable);

    MemberStateDocument memberState = memberStateService.getMemberStateDocument(currentMemberId);

    return FeedDetailDto.of(baseFeedDto, comments, memberState);
  }

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
            locationInfo.coordinate(),
            locationInfo.address(),
            locationInfo.locationName()
        ))
        .orElse(null);

    // FeedDocument
    FeedDocument feedDocument = FeedDocument.of(
        event.feedId(),
        author,
        event.content(),
        medias,
        location,
        event.category(),
        event.language(),
        Collections.emptyList()
    );

    // save
    feedReadRepository.save(feedDocument);
    log.info("FeedDocument created for feedId: {}", event.feedId());
  }

  @Override
  public void syncUpdatedFeed(FeedUpdatedEvent event) {
    log.info("FeedUpdatedEvent received for feedId: {}", event.feedId());

    feedReadRepository.findByFeedId(event.feedId()).ifPresent(feedDocument -> {
      feedDocument.updateContent(event.content());
      feedReadRepository.save(feedDocument);
      log.info("FeedDocument updated for feedId: {}", event.feedId());
    });
  }
}
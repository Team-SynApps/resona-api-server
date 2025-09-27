package com.synapps.resona.feed.service;

import com.oracle.bmc.model.BmcException;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.dto.FeedDetailDto;
import com.synapps.resona.feed.dto.FeedDto;
import com.synapps.resona.feed.dto.FeedMetaData;
import com.synapps.resona.feed.dto.FeedSortBy;
import com.synapps.resona.feed.dto.LocationRequest;
import com.synapps.resona.feed.dto.condition.DefaultFeedSearchCondition;
import com.synapps.resona.feed.dto.condition.MemberFeedSearchCondition;
import com.synapps.resona.feed.dto.request.FeedRequest;
import com.synapps.resona.feed.dto.request.FeedUpdateRequest;
import com.synapps.resona.feed.entity.Feed;
import com.synapps.resona.feed.entity.Location;
import com.synapps.resona.feed.exception.FeedException;
import com.synapps.resona.feed.repository.FeedRepository;
import com.synapps.resona.feed.repository.LocationRepository;
import com.synapps.resona.feed.repository.dsl.FeedExpressions;
import com.synapps.resona.feed.repository.strategy.FeedQueryStrategy;
import com.synapps.resona.file.ObjectStorageService;
import com.synapps.resona.file.dto.FileMetadataDto;
import com.synapps.resona.dto.CursorResult;
import com.synapps.resona.media.dto.FeedImageDto;
import com.synapps.resona.media.entity.FeedMedia;
import com.synapps.resona.media.repository.FeedMediaRepository;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.service.MemberService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedService {

  private final FeedRepository feedRepository;
  private final FeedMediaRepository feedMediaRepository;
  private final MemberRepository memberRepository;
  private final LocationRepository locationRepository;
  private final ObjectStorageService objectStorageService;
  private final MemberService memberService;

  private final FeedQueryStrategyFactory feedQueryStrategyFactory;
  private final FeedExpressions feedExpressions;

  private static final Logger logger = LoggerFactory.getLogger(FeedService.class);

  @Transactional
  public FeedDto updateFeed(Long feedId, Long memberId, FeedUpdateRequest feedRequest) {
    // 예외처리 해줘야 함
    Feed feed = feedRepository.findFeedWithImagesByFeedId(feedId).orElseThrow(FeedException::feedNotFoundException);
    feed.updateContent(feedRequest.getContent());

    FeedMetaData feedMetaData = feedExpressions.fetchFeedMetaData(feedId, memberId);

    return FeedDto.from(FeedDetailDto.of(feed, feedMetaData));
  }

  @Transactional
  public FeedDto readFeed(Long feedId, Long memberId) {
    FeedDetailDto feed = feedRepository.findFeedDetailById(feedId, memberId);

    return FeedDto.from(feed);
  }

  @Transactional(readOnly = true)
  public CursorResult<FeedDto> getFeedsByCursorAndMemberId(Long viewerId, Long targetMemberId, String cursor, int size) {
    Pageable pageable = PageRequest.of(0, size + 1);

    MemberFeedSearchCondition condition =
        MemberFeedSearchCondition.of(
            viewerId,
            targetMemberId,
            cursor != null ? LocalDateTime.parse(cursor) : null,
            FeedSortBy.LATEST
        );

    FeedQueryStrategy<MemberFeedSearchCondition> strategy = feedQueryStrategyFactory.findStrategy(MemberFeedSearchCondition.class);
    List<FeedDto> feeds = strategy.findFeeds(condition, condition.getCursor(), pageable, viewerId);

    return createCursorResult(feeds, size);
  }

  @Transactional(readOnly = true)
  public CursorResult<FeedDto> getFeedsByCursor(Long viewerId, String cursor, int size) {
    Pageable pageable = PageRequest.of(0, size + 1);

    DefaultFeedSearchCondition condition =
        DefaultFeedSearchCondition.of(
            viewerId,
            cursor != null ? LocalDateTime.parse(cursor) : null,
            FeedSortBy.LATEST);

    FeedQueryStrategy<DefaultFeedSearchCondition> strategy = feedQueryStrategyFactory.findStrategy(DefaultFeedSearchCondition.class);
    List<FeedDto> feeds = strategy.findFeeds(condition, condition.getCursor(), pageable, viewerId);

    return createCursorResult(feeds, size);
  }


  @Transactional
  public void deleteFeed(Long feedId) {
    Feed feed = feedRepository.findById(feedId).orElseThrow(FeedException::feedNotFoundException);
    feed.softDelete();
  }

  /**
   * @param metadataList
   * @param feedRequest
   * @return
   */
  @Transactional
  public FeedDto registerFeed(List<FileMetadataDto> metadataList, FeedRequest feedRequest) {
    String email = memberService.getMemberEmail();
    Member member = memberRepository.findByEmail(email).orElseThrow();

    // save feed entity
    Feed feed = Feed.of(member, feedRequest.getContent(), feedRequest.getCategory(), feedRequest.getLanguageCode());
    feedRepository.save(feed);

    // finalizing feedImages: move buffer bucket images to disk bucket
    List<FeedImageDto> finalizedFeed = metadataList.parallelStream()
        .map(metadata -> {
          try {
            String finalFileName = String.format("%s/%s/%s?%s&%s",
                member.getId(),
                "feed",
                feed.getId(),
                "width=" + metadata.getWidth() + "&height=" + metadata.getHeight(),
                "index=" + metadata.getIndex());
            String diskUrl = objectStorageService.copyToDisk(metadata, finalFileName);

            return FeedImageDto.builder()
                .url(diskUrl)
                .index(metadata.getIndex())
                .build();
          } catch (BmcException e) {
            logger.error("Failed to copy file: {}", metadata.getOriginalFileName(), e);
            throw new RuntimeException("Failed to copy file: " + metadata.getOriginalFileName(), e);
          }
        })
        .toList();

    finalizedFeed.forEach(feedTempData -> {
      FeedMedia feedMedia = FeedMedia.of(feed, "IMAGE", feedTempData.getUrl(),
          feedTempData.getIndex());
      feedMediaRepository.save(feedMedia);
    });

    // save location entity if exist
    LocationRequest locationRequest = feedRequest.getLocation();

    if (locationRequest.getAddress() != null) {
      Location location = Location.of(feed, locationRequest.getCoordinate(),
          locationRequest.getAddress(), locationRequest.getName());
      locationRepository.save(location);
    }

    FeedMetaData newFeedMetaData = new FeedMetaData(0, 0, false, false);

    return FeedDto.from(FeedDetailDto.of(feed, newFeedMetaData));
  }

  private CursorResult<FeedDto> createCursorResult(List<FeedDto> feeds, int size) {
    boolean hasNext = feeds.size() > size;
    List<FeedDto> content = hasNext ? feeds.subList(0, size) : feeds;

    String nextCursor = null;
    if (hasNext) {
      nextCursor = content.get(content.size() - 1).getCreatedAt().toString();
    }

    return new CursorResult<>(content, hasNext, nextCursor);
  }
}

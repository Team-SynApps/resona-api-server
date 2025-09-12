package synapps.resona.api.socialMedia.feed.service;

import com.oracle.bmc.model.BmcException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.external.file.ObjectStorageService;
import synapps.resona.api.external.file.dto.FileMetadataDto;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.member.entity.member.Member;
import synapps.resona.api.member.repository.member.MemberRepository;
import synapps.resona.api.member.service.MemberService;
import synapps.resona.api.socialMedia.feed.dto.FeedDetailDto;
import synapps.resona.api.socialMedia.feed.dto.condition.DefaultFeedSearchCondition;
import synapps.resona.api.socialMedia.feed.dto.FeedSortBy;
import synapps.resona.api.socialMedia.feed.dto.condition.MemberFeedSearchCondition;
import synapps.resona.api.socialMedia.feed.dto.FeedDto;
import synapps.resona.api.socialMedia.feed.dto.request.FeedRequest;
import synapps.resona.api.socialMedia.feed.dto.request.FeedUpdateRequest;
import synapps.resona.api.socialMedia.media.dto.FeedImageDto;
import synapps.resona.api.socialMedia.feed.dto.LocationRequest;
import synapps.resona.api.socialMedia.feed.entity.Feed;
import synapps.resona.api.socialMedia.feed.entity.Location;
import synapps.resona.api.socialMedia.media.entity.FeedMedia;
import synapps.resona.api.socialMedia.feed.exception.FeedException;
import synapps.resona.api.socialMedia.feed.repository.strategy.FeedQueryStrategy;
import synapps.resona.api.socialMedia.media.repository.FeedMediaRepository;
import synapps.resona.api.socialMedia.feed.repository.FeedRepository;
import synapps.resona.api.socialMedia.feed.repository.LocationRepository;

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

  private final Logger logger = LogManager.getLogger(FeedService.class);

  @Transactional
  public FeedDto updateFeed(Long feedId, FeedUpdateRequest feedRequest) {
    // 예외처리 해줘야 함
    Feed feed = feedRepository.findFeedWithImagesByFeedId(feedId).orElseThrow(FeedException::feedNotFoundException);
    feed.updateContent(feedRequest.getContent());

    return FeedDto.from(feed);
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
  public FeedDto deleteFeed(Long feedId) {
    Feed feed = feedRepository.findById(feedId).orElseThrow(FeedException::feedNotFoundException);
    feed.softDelete();
    return FeedDto.from();
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
    Feed feed = Feed.of(member, feedRequest.getContent(), feedRequest.getCategory(), feedRequest.getLanguage());
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

    return FeedDto.from(feed, finalizedFeed);
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

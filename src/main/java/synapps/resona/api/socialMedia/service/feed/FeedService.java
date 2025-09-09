package synapps.resona.api.socialMedia.service.feed;

import com.oracle.bmc.model.BmcException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import synapps.resona.api.socialMedia.dto.feed.FeedDetailDto;
import synapps.resona.api.socialMedia.dto.feed.condition.DefaultFeedSearchCondition;
import synapps.resona.api.socialMedia.dto.feed.FeedSortBy;
import synapps.resona.api.socialMedia.dto.feed.condition.MemberFeedSearchCondition;
import synapps.resona.api.socialMedia.dto.feed.FeedDto;
import synapps.resona.api.socialMedia.dto.media.FeedMediaDto;
import synapps.resona.api.socialMedia.dto.feed.FeedWithMediaDto;
import synapps.resona.api.socialMedia.dto.feed.request.FeedRequest;
import synapps.resona.api.socialMedia.dto.feed.request.FeedUpdateRequest;
import synapps.resona.api.socialMedia.dto.feed.response.FeedResponse;
import synapps.resona.api.socialMedia.dto.media.FeedImageDto;
import synapps.resona.api.socialMedia.dto.location.LocationRequest;
import synapps.resona.api.socialMedia.entity.feed.Feed;
import synapps.resona.api.socialMedia.entity.feed.Location;
import synapps.resona.api.socialMedia.entity.media.FeedMedia;
import synapps.resona.api.socialMedia.exception.FeedException;
import synapps.resona.api.socialMedia.repository.feed.strategy.FeedQueryStrategy;
import synapps.resona.api.socialMedia.repository.media.FeedMediaRepository;
import synapps.resona.api.socialMedia.repository.feed.FeedRepository;
import synapps.resona.api.socialMedia.repository.feed.LocationRepository;

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
  public FeedResponse updateFeed(Long feedId, FeedUpdateRequest feedRequest) {
    // 예외처리 해줘야 함
    Feed feed = feedRepository.findFeedWithImagesByFeedId(feedId).orElseThrow(FeedException::feedNotFoundException);
    feed.updateContent(feedRequest.getContent());

    return FeedResponse.from(feed);
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
  public List<FeedWithMediaDto> getFeedsWithMediaAndLikeCount(Long memberId) {
    List<Feed> feeds = feedRepository.findFeedsWithImagesByMemberId(memberId);
    Map<Long, Integer> likeCountMap = feedRepository.countLikesByMemberId(memberId).stream()
        .collect(Collectors.toMap(
            row -> (Long) row[0],
            row -> ((Long) row[1]).intValue()
        ));

    return feeds.stream()
        .map(feed -> new FeedWithMediaDto(
            feed.getId(),
            feed.getContent(),
            likeCountMap.getOrDefault(feed.getId(), 0),
            feed.getImages().stream()
                .map(FeedMediaDto::from)
                .toList()
        ))
        .toList();
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
  public Feed deleteFeed(Long feedId) {
    Feed feed = feedRepository.findById(feedId).orElseThrow(FeedException::feedNotFoundException);
    feed.softDelete();
    return feed;
  }

  /**
   * @param metadataList
   * @param feedRequest
   * @return
   */
  @Transactional
  public FeedResponse registerFeed(List<FileMetadataDto> metadataList, FeedRequest feedRequest) {
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

    return FeedResponse.from(feed, finalizedFeed);
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

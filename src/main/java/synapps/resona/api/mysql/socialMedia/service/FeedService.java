package synapps.resona.api.mysql.socialMedia.service;

import com.oracle.bmc.model.BmcException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.external.file.ObjectStorageService;
import synapps.resona.api.external.file.dto.FileMetadataDto;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.exception.MemberException;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.socialMedia.dto.feed.FeedMediaDto;
import synapps.resona.api.mysql.socialMedia.dto.feed.FeedWithMediaDto;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedRequest;
import synapps.resona.api.mysql.socialMedia.dto.feed.request.FeedUpdateRequest;
import synapps.resona.api.mysql.socialMedia.dto.feed.response.FeedReadResponse;
import synapps.resona.api.mysql.socialMedia.dto.feed.response.FeedResponse;
import synapps.resona.api.mysql.socialMedia.dto.feedImage.FeedImageDto;
import synapps.resona.api.mysql.socialMedia.dto.location.LocationRequest;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.entity.feed.Location;
import synapps.resona.api.mysql.socialMedia.entity.feedMedia.FeedMedia;
import synapps.resona.api.mysql.socialMedia.exception.FeedException;
import synapps.resona.api.mysql.socialMedia.repository.FeedMediaRepository;
import synapps.resona.api.mysql.socialMedia.repository.FeedRepository;
import synapps.resona.api.mysql.socialMedia.repository.LocationRepository;

@Service
@RequiredArgsConstructor
public class FeedService {

  private final FeedRepository feedRepository;
  private final FeedMediaRepository feedMediaRepository;
  private final MemberRepository memberRepository;
  private final LocationRepository locationRepository;
  private final ObjectStorageService objectStorageService;
  private final MemberService memberService;

  private final Logger logger = LogManager.getLogger(FeedService.class);

  @Transactional
  public FeedResponse updateFeed(Long feedId, FeedUpdateRequest feedRequest) {
    // 예외처리 해줘야 함
    Feed feed = feedRepository.findFeedWithImagesByFeedId(feedId).orElseThrow(FeedException::feedNotFoundException);
    feed.updateContent(feedRequest.getContent());

    return FeedResponse.from(feed);
  }

  @Transactional
  public FeedReadResponse readFeed(Long feedId) {
    Feed feed = feedRepository.findFeedWithImagesByFeedId(feedId).orElseThrow(FeedException::feedNotFoundException);

    return FeedReadResponse.from(feed);
  }

  public CursorResult<FeedReadResponse> getFeedsByCursorAndMemberId(String cursor, int size,
      Long memberId) {
    LocalDateTime cursorTime = (cursor != null) ?
        LocalDateTime.parse(cursor) : LocalDateTime.now();

    Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "createdAt"));

    List<Feed> feeds = feedRepository.findFeedsByCursorAndMemberId(memberId, cursorTime, pageable);

    boolean hasNext = feeds.size() > size;
    if (hasNext) {
      feeds = feeds.subList(0, size);
    }

    String nextCursor = hasNext ?
        feeds.get(feeds.size() - 1).getCreatedAt().toString() : null;

    return new CursorResult<>(
        feeds.stream()
            .map(FeedReadResponse::from)
            .collect(Collectors.toList()),
        hasNext,
        nextCursor
    );
  }


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


  public CursorResult<FeedReadResponse> getFeedsByCursor(String cursor, int size) {
    LocalDateTime cursorTime = (cursor != null) ?
        LocalDateTime.parse(cursor) : LocalDateTime.now();

    Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "createdAt"));

    List<Feed> feeds = feedRepository.findFeedsByCursor(cursorTime, pageable);
    boolean hasNext = feeds.size() > size;
    if (hasNext) {
      feeds.remove(feeds.size() - 1);
    }

    String nextCursor = hasNext ?
        feeds.get(feeds.size() - 1).getCreatedAt().toString() : null;

    return new CursorResult<>(
        feeds.stream()
            .map(FeedReadResponse::from)
            .collect(Collectors.toList()),
        hasNext,
        nextCursor
    );
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
    Feed feed = Feed.of(member, feedRequest.getContent(), feedRequest.getCategory());
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
}

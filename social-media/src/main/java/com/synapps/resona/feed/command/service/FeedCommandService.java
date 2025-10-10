package com.synapps.resona.feed.command.service;

import com.oracle.bmc.model.BmcException;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.exception.MemberException;
import com.synapps.resona.feed.command.entity.MediaType;
import com.synapps.resona.feed.dto.FeedDetailDto;
import com.synapps.resona.feed.dto.FeedCreateDto;
import com.synapps.resona.feed.dto.FeedMetaData;
import com.synapps.resona.feed.dto.request.LocationRequest;
import com.synapps.resona.feed.dto.request.FeedRequest;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.entity.Location;
import com.synapps.resona.feed.event.FeedCreatedEvent;
import com.synapps.resona.feed.event.FeedCreatedEvent.LocationInfo;
import com.synapps.resona.feed.exception.FeedException;
import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.feed.command.repository.LocationRepository;
import com.synapps.resona.file.ObjectStorageService;
import com.synapps.resona.file.dto.FileMetadataDto;
import com.synapps.resona.feed.dto.FeedImageDto;
import com.synapps.resona.feed.command.entity.FeedMedia;
import com.synapps.resona.feed.command.repository.FeedMediaRepository;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.service.MemberService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedCommandService {

  private final FeedRepository feedRepository;
  private final FeedMediaRepository feedMediaRepository;
  private final MemberRepository memberRepository;
  private final LocationRepository locationRepository;
  private final ObjectStorageService objectStorageService;
  private final MemberService memberService;
  private final ApplicationEventPublisher eventPublisher;

  private static final Logger logger = LoggerFactory.getLogger(FeedCommandService.class);

  public Feed getFeed(Long feedId) {
    return feedRepository.findById(feedId).orElseThrow(FeedException::feedNotFoundException);
  }

  @Transactional
  public FeedCreateDto registerFeed(List<FileMetadataDto> metadataList, FeedRequest feedRequest) {
    String email = memberService.getMemberEmail();
    Member member = memberRepository.findByEmail(email).orElseThrow(MemberException::memberNotFound);

    // 피드 1차 저장
    Feed feed = Feed.of(member, feedRequest.getContent(), feedRequest.getCategory(), feedRequest.getLanguageCode());
    feedRepository.save(feed);

    // 피드 이미지 버킷 이동(버퍼 버킷 -> 이미지 버킷)
    List<FeedImageDto> finalizedImages = metadataList.parallelStream()
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

    finalizedImages.forEach(feedTempData -> {
      FeedMedia feedMedia = FeedMedia.of(feed, "IMAGE", feedTempData.getUrl(),
          feedTempData.getIndex());
      feedMediaRepository.save(feedMedia);
    });

    // save location entity if exist
    LocationRequest locationRequest = feedRequest.getLocation();

    if (locationRequest != null && locationRequest.getFormattedAddress() != null) {
      if (locationRepository.existsByPlaceId(locationRequest.getPlaceId())) {
        Location location = locationRepository.findByPlaceId(locationRequest.getPlaceId()).orElseThrow();
        feed.addLocation(location);
      } else {
        Location location = Location.of(locationRequest.getPlaceId(),
            locationRequest.getDisplayName(), locationRequest.getFormattedAddress(),
            locationRequest.getLocation().getLatitude(), locationRequest.getLocation().getLongitude(),
            locationRequest.getPrimaryType());
        locationRepository.save(location);
        feed.addLocation(location);
      }
    }

    publishFeedCreatedEvent(feed, member, finalizedImages, locationRequest);

    FeedMetaData newFeedMetaData = new FeedMetaData(0, 0, false, false);

    return FeedCreateDto.from(FeedDetailDto.of(feed, newFeedMetaData));
  }

  private void publishFeedCreatedEvent(Feed feed, Member member,
      List<FeedImageDto> finalizedFeedImages, LocationRequest locationRequest) {

    boolean isCelebrity = memberService.isCelebrity(member);

    FeedCreatedEvent.AuthorInfo authorInfo = new FeedCreatedEvent.AuthorInfo(
        member.getId(),
        member.getProfile().getNickname(),
        member.getProfile().getProfileImageUrl(),
        isCelebrity,
        member.getProfile().getCountryOfResidence()
    );

    List<FeedCreatedEvent.MediaInfo> mediaInfos = finalizedFeedImages.stream()
        .map(mediaDto -> new FeedCreatedEvent.MediaInfo(
            MediaType.IMAGE,
            mediaDto.getUrl(),
            mediaDto.getIndex()
        ))
        .toList();

    Optional<LocationInfo> locationInfo = Optional.ofNullable(locationRequest)
        .map(loc -> new FeedCreatedEvent.LocationInfo(
            loc.getPlaceId(),
            loc.getDisplayName(),
            loc.getFormattedAddress(),
            new FeedCreatedEvent.LocationInfo.GeoLocation(
                loc.getLocation().getLatitude(),
                loc.getLocation().getLongitude()
            ),
            loc.getPrimaryType()
        ));

    FeedCreatedEvent feedCreatedEvent = new FeedCreatedEvent(
        feed.getId(),
        feed.getContent(),
        feed.getCategory(),
        feed.getLanguage(),
        authorInfo,
        mediaInfos,
        locationInfo,
        LocalDateTime.now()
    );

    eventPublisher.publishEvent(feedCreatedEvent);
  }

  @Transactional
  public void deleteFeed(Long feedId) {
    Feed feed = feedRepository.findById(feedId).orElseThrow(FeedException::feedNotFoundException);
    feed.softDelete();
  }

  //
//  @Transactional
//  public FeedCreateDto updateFeed(Long feedId, Long memberId, FeedUpdateRequest feedRequest) {
//    Feed feed = feedRepository.findFeedWithImagesByFeedId(feedId).orElseThrow(FeedException::feedNotFoundException);
//    feed.updateContent(feedRequest.getContent());
//
//    publishFeedUpdatedEvent(feed);
//
//    FeedMetaData feedMetaData = feedExpressions.fetchFeedMetaData(feedId, memberId);
//    return Fee
//    dCreateDto.from(FeedDetailDto.of(feed, feedMetaData));
//  }

//  private void publishFeedUpdatedEvent(Feed feed) {
//    FeedUpdatedEvent event = new FeedUpdatedEvent(
//        feed.getId(),
//        feed.getContent(),
//        LocalDateTime.now()
//    );
//    eventPublisher.publishEvent(event);
//  }

}

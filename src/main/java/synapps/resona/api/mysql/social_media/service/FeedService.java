package synapps.resona.api.mysql.social_media.service;

import com.oracle.bmc.model.BmcException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.external.file.ObjectStorageService;
import synapps.resona.api.external.file.dto.FileMetadataDto;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.social_media.dto.feed.FeedTempDto;
import synapps.resona.api.mysql.social_media.dto.feed.request.FeedRequest;
import synapps.resona.api.mysql.social_media.dto.feed.request.FeedUpdateRequest;
import synapps.resona.api.mysql.social_media.dto.feed.response.FeedPostResponse;
import synapps.resona.api.mysql.social_media.dto.location.LocationRequest;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.entity.FeedMedia;
import synapps.resona.api.mysql.social_media.entity.Location;
import synapps.resona.api.mysql.social_media.exception.FeedNotFoundException;
import synapps.resona.api.mysql.social_media.repository.FeedMediaRepository;
import synapps.resona.api.mysql.social_media.repository.FeedRepository;
import synapps.resona.api.mysql.social_media.repository.LocationRepository;

import java.time.LocalDateTime;
import java.util.List;

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
    public Feed updateFeed(FeedUpdateRequest feedRequest) throws FeedNotFoundException {
        // 예외처리 해줘야 함
        Feed feed = feedRepository.findById(feedRequest.getFeedId()).orElseThrow(FeedNotFoundException::new);
        feed.updateContent(feedRequest.getContent());
        return feed;
    }

    public Feed readFeed(Long feedId) throws FeedNotFoundException {
        return feedRepository.findById(feedId).orElseThrow(FeedNotFoundException::new);
    }

    @Transactional
    public Feed deleteFeed(Long feedId) throws FeedNotFoundException {
        Feed feed  = feedRepository.findById(feedId).orElseThrow(FeedNotFoundException::new);
        feed.softDelete();
        return feed;
    }

    /**
     * 불필요하게 피드를 한번 더 조회해서 반환하고 있음. 부분 수정해야 함
     * @param metadataList
     * @param feedRequest
     * @return
     */
    @Transactional
    public FeedPostResponse registerFeed(List<FileMetadataDto> metadataList, FeedRequest feedRequest) {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        // save feed entity
        Feed feed = Feed.of(member, feedRequest.getContent(),feedRequest.getCategory(), LocalDateTime.now(), LocalDateTime.now());
        feedRepository.save(feed);

        // finalizing feedImages: move buffer bucket images to disk bucket
        List<FeedTempDto> finalizedFeed = metadataList.parallelStream()
                .map(metadata -> {
                    try {
                        String finalFileName = String.format("%s/%s/%s?%s&%s",
                                member.getId(),
                                "feed",
                                feed.getId(),
                                "width=" + metadata.getWidth() + "&height=" + metadata.getHeight(),
                                "index=" + metadata.getIndex());
                        String diskUrl = objectStorageService.copyToDisk(metadata,finalFileName);

                        return FeedTempDto.builder()
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
            FeedMedia feedMedia = FeedMedia.of(feed, "IMAGE", feedTempData.getUrl(),feedTempData.getIndex(), LocalDateTime.now(), LocalDateTime.now());
            feedMediaRepository.save(feedMedia);
        });

        // save location entity if exist
        LocationRequest locationRequest = feedRequest.getLocation();

        if(locationRequest.getAddress()!= null) {
            Location location = Location.of(feed, locationRequest.getCoordinate(), locationRequest.getAddress(), locationRequest.getName(), LocalDateTime.now(), LocalDateTime.now());
            locationRepository.save(location);
        }

        return FeedPostResponse.builder()
                .id(feed.getId().toString())
                .feedTempDtos(finalizedFeed)
                .content(feed.getContent()).build();
    }
}

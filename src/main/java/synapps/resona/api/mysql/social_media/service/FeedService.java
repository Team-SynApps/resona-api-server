package synapps.resona.api.mysql.social_media.service;

import com.oracle.bmc.model.BmcException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.external.file.ObjectStorageService;
import synapps.resona.api.external.file.dto.FileMetadataDto;
import synapps.resona.api.global.dto.CursorResult;
import synapps.resona.api.global.utils.DateTimeUtil;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.MemberRepository;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.social_media.dto.feed.FeedImageDto;
import synapps.resona.api.mysql.social_media.dto.feed.request.FeedRequest;
import synapps.resona.api.mysql.social_media.dto.feed.request.FeedUpdateRequest;
import synapps.resona.api.mysql.social_media.dto.feed.response.FeedResponse;
import synapps.resona.api.mysql.social_media.dto.feed.response.FeedReadResponse;
import synapps.resona.api.mysql.social_media.dto.location.LocationRequest;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.entity.FeedMedia;
import synapps.resona.api.mysql.social_media.entity.Location;
import synapps.resona.api.mysql.social_media.exception.FeedNotFoundException;
import synapps.resona.api.mysql.social_media.repository.FeedMediaRepository;
import synapps.resona.api.mysql.social_media.repository.FeedRepository;
import synapps.resona.api.mysql.social_media.repository.LocationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public FeedResponse updateFeed(FeedUpdateRequest feedRequest) throws FeedNotFoundException {
        // 예외처리 해줘야 함
        Feed feed = feedRepository.findById(feedRequest.getFeedId()).orElseThrow(FeedNotFoundException::new);
        feed.updateContent(feedRequest.getContent());

        List<FeedImageDto> feedImageDtos =new ArrayList<>();
        for (FeedMedia media : feed.getImages()) {
            FeedImageDto imageDto = FeedImageDto.from(media);
            feedImageDtos.add(imageDto);
        }
        return FeedResponse.builder()
                .id(feed.getId().toString())
                .content(feed.getContent())
                .feedImageDtos(feedImageDtos)
                .createdAt(DateTimeUtil.localDateTimeToString(feed.getCreatedAt()))
                .build();
    }

    @Transactional
    public FeedReadResponse readFeed(Long feedId) throws FeedNotFoundException {
        Feed feed = feedRepository.findById(feedId).orElseThrow(FeedNotFoundException::new);
        List<FeedMedia> feedMedias = feed.getImages();
        List<FeedImageDto> feedImageDtos = new ArrayList<>();

        for (FeedMedia feedMedia : feedMedias) {
            feedImageDtos.add(FeedImageDto.builder().url(feedMedia.getUrl()).index(feedMedia.getIndex()).build());
        }

        return FeedReadResponse.builder()
                .feedImageDtos(feedImageDtos)
                .id(feed.getId().toString())
                .createdAt(feed.getCreatedAt().toString())
                .content(feed.getContent())
                .build();
    }

    @Transactional
    public List<FeedReadResponse> readAllFeeds() throws FeedNotFoundException {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        List<Feed> feeds = feedRepository.findAllByMember(member);
        List<FeedReadResponse> feedReadResponses = new ArrayList<>();
        for (Feed feed : feeds) {
            List<FeedMedia> feedMedias = feed.getImages();
            List<FeedImageDto> feedImageDtos = new ArrayList<>();

            for (FeedMedia feedMedia : feedMedias) {
                feedImageDtos.add(FeedImageDto.builder().url(feedMedia.getUrl()).index(feedMedia.getIndex()).build());
            }
            feedReadResponses.add(FeedReadResponse.builder()
                    .feedImageDtos(feedImageDtos)
                    .id(feed.getId().toString())
                    .content(feed.getContent())
                    .build());
        }
        return feedReadResponses;
    }

    public CursorResult<FeedReadResponse> getFeedsByCursor(String cursor, int size) {
        LocalDateTime cursorTime = cursor != null ?
                LocalDateTime.parse(cursor) : LocalDateTime.now();

        List<Feed> feeds = feedRepository.findFeedsByCursor(cursorTime, size + 1);
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
    public Feed deleteFeed(Long feedId) throws FeedNotFoundException {
        Feed feed = feedRepository.findById(feedId).orElseThrow(FeedNotFoundException::new);
        feed.softDelete();
        return feed;
    }

    /**
     * 불필요하게 피드를 한번 더 조회해서 반환하고 있음. 부분 수정해야 함
     *
     * @param metadataList
     * @param feedRequest
     * @return
     */
    @Transactional
    public FeedResponse registerFeed(List<FileMetadataDto> metadataList, FeedRequest feedRequest) {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        // save feed entity
        Feed feed = Feed.of(member, feedRequest.getContent(), feedRequest.getCategory(), LocalDateTime.now(), LocalDateTime.now());
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
            FeedMedia feedMedia = FeedMedia.of(feed, "IMAGE", feedTempData.getUrl(), feedTempData.getIndex(), LocalDateTime.now(), LocalDateTime.now());
            feedMediaRepository.save(feedMedia);
        });

        // save location entity if exist
        LocationRequest locationRequest = feedRequest.getLocation();

        if (locationRequest.getAddress() != null) {
            Location location = Location.of(feed, locationRequest.getCoordinate(), locationRequest.getAddress(), locationRequest.getName(), LocalDateTime.now(), LocalDateTime.now());
            locationRepository.save(location);
        }

        return FeedResponse.builder()
                .id(feed.getId().toString())
                .feedImageDtos(finalizedFeed)
                .createdAt(feed.getCreatedAt().toString())
                .content(feed.getContent()).build();
    }
}

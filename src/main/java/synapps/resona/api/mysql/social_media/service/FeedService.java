package synapps.resona.api.mysql.social_media.service;

import com.oracle.bmc.model.BmcException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synapps.resona.api.external.file.ObjectStorageService;
import synapps.resona.api.external.file.dto.FileMetadataDto;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.service.MemberService;
import synapps.resona.api.mysql.social_media.dto.feed.request.FeedRequest;
import synapps.resona.api.mysql.social_media.dto.feed.request.FeedUpdateRequest;
import synapps.resona.api.mysql.social_media.entity.Feed;
import synapps.resona.api.mysql.social_media.entity.FeedMedia;
import synapps.resona.api.mysql.social_media.exception.FeedNotFoundException;
import synapps.resona.api.mysql.social_media.repository.FeedMediaRepository;
import synapps.resona.api.mysql.social_media.repository.FeedRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final FeedMediaRepository feedMediaRepository;
    private final ObjectStorageService objectStorageService;
    private final MemberService memberService;

    private final Logger logger = LogManager.getLogger(FeedService.class);

    @Transactional
    public Feed register(FeedRequest feedRequest) {

        return feedRepository.save(feed);
    }

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

    @Transactional
    public List<String> registerFeed(List<FileMetadataDto> metadataList, FeedRequest feedRequest) {
        Member member = memberService.getMember();
        Feed feed = Feed.of(member, feedRequest.getContent(),feedRequest.getCategory(), LocalDateTime.now(), LocalDateTime.now());
        feedRepository.save(feed);
        return metadataList.parallelStream()
                .map(metadata -> {
                    try {
                        // 최종 이름 바꿔야 함.
                        String finalFileName = UUID.randomUUID().toString();
                        String diskCreatedUrl = objectStorageService.copyToDisk(metadata,finalFileName);

                        FeedMedia feedMedia = FeedMedia.of(feed, "IMAGE", diskCreatedUrl, LocalDateTime.now(), LocalDateTime.now());
                        feedMediaRepository.save(feedMedia);

                        return diskCreatedUrl;
                    } catch (BmcException e) {
                        logger.error("Failed to copy file: {}", metadata.getOriginalFileName(), e);
                        throw new RuntimeException("Failed to copy file: " + metadata.getOriginalFileName(), e);
                    }
                })
                .collect(Collectors.toList());
    }
}

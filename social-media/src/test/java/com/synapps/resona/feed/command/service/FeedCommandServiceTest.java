package com.synapps.resona.feed.command.service;

import static com.synapps.resona.support.fixture.FeedFixture.CATEGORY;
import static com.synapps.resona.support.fixture.FeedFixture.CONTENT;
import static com.synapps.resona.support.fixture.FeedFixture.FINAL_FILE_URL;
import static com.synapps.resona.support.fixture.FeedFixture.LOCATION_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.entity.FeedMedia;
import com.synapps.resona.feed.command.entity.Location;
import com.synapps.resona.feed.command.repository.FeedMediaRepository;
import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.feed.command.repository.LocationRepository;
import com.synapps.resona.feed.dto.FeedCreateDto;
import com.synapps.resona.feed.event.FeedCreatedEvent;
import com.synapps.resona.file.ObjectStorageService;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.service.MemberService;
import com.synapps.resona.support.ServiceLayerTest;
import com.synapps.resona.support.fixture.FeedFixture;
import fixture.MemberFixture;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.transaction.annotation.Transactional;
import support.config.TestContainerConfig;
import support.database.DatabaseCleaner;

@ServiceLayerTest
@Import({
    FeedCommandService.class,
    DatabaseCleaner.class,
    TestContainerConfig.class
})
class FeedCommandServiceTest {

    @Autowired
    private FeedCommandService feedCommandService;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private FeedMediaRepository feedMediaRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    @MockBean
    private ObjectStorageService objectStorageService;

    @MockBean
    private MemberService memberService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
        testMember = MemberFixture.createProfileTestMember("test@gmail.com");
        memberRepository.save(testMember);
    }

    @Test
    @DisplayName("새로운 피드를 성공적으로 등록한다.")
    void registerFeed_Success() {
        var feedRequest = FeedFixture.createFeedRequest();
        var metadataList = List.of(FeedFixture.createFileMetadataDto());

        when(memberService.getMemberEmail()).thenReturn(testMember.getEmail());
        when(objectStorageService.copyToDisk(any(), any())).thenReturn(FINAL_FILE_URL);

        // when
        FeedCreateDto result = feedCommandService.registerFeed(metadataList, feedRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFeedId()).isNotNull();

        Optional<Feed> savedFeedOpt = feedRepository.findById(result.getFeedId());
        assertThat(savedFeedOpt).isPresent();
        Feed savedFeed = savedFeedOpt.get();
        assertThat(savedFeed.getContent()).isEqualTo(CONTENT);
        assertThat(savedFeed.getMember().getId()).isEqualTo(testMember.getId());
        assertThat(savedFeed.getCategory().name()).isEqualTo(CATEGORY);

        List<FeedMedia> savedMedia = feedMediaRepository.findAll();
        assertThat(savedMedia).hasSize(1);
        assertThat(savedMedia.get(0).getUrl()).isEqualTo(FINAL_FILE_URL);

        long eventCount = applicationEvents.stream(FeedCreatedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }

    @Test
    @DisplayName("피드를 성공적으로 소프트 삭제한다.")
    @Transactional
    void deleteFeed_Success() {
        Feed feed = FeedFixture.createDeletableFeed(testMember);
        feedRepository.save(feed);
        Long feedId = feed.getId();

        // when
        feedCommandService.deleteFeed(feedId);

        // then
        Optional<Feed> result = feedRepository.findById(feedId);
        assertThat(result).isPresent();
        assertThat(result.get().isDeleted()).isTrue();
    }
}
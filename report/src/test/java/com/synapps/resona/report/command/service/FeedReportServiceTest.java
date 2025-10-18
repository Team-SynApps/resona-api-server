
package com.synapps.resona.report.command.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.service.FeedCommandService;
import com.synapps.resona.report.command.entity.FeedReport;
import com.synapps.resona.report.command.repository.FeedReportRepository;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.event.FeedReportedEvent;
import com.synapps.resona.command.repository.member.MemberRepository;
import com.synapps.resona.command.service.MemberService;
import com.synapps.resona.report.support.ServiceLayerTest;
import com.synapps.resona.report.support.fixture.FeedFixture;
import fixture.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.ApplicationEvents;
import support.config.TestContainerConfig;
import support.database.DatabaseCleaner;

@ServiceLayerTest
@Import({FeedReportService.class, DatabaseCleaner.class, TestContainerConfig.class})
@Disabled
class FeedReportServiceTest {

    @Autowired
    private FeedReportService feedReportService;

    @Autowired
    private FeedReportRepository feedReportRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @MockBean
    private MemberService memberService;

    @MockBean
    private FeedCommandService feedCommandService;

    @Autowired
    private ApplicationEvents applicationEvents;

    private Member reporter;
    private Member reported;
    private Feed testFeed;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
        reporter = MemberFixture.createProfileTestMember("reporter@gmail.com");
        reported = MemberFixture.createProfileTestMember("reported@gmail.com");
        memberRepository.saveAll(java.util.List.of(reporter, reported));

        testFeed = FeedFixture.createDeletableFeed(reported);
        feedRepository.save(testFeed);
    }

    @Test
    @DisplayName("피드를 성공적으로 신고한다.")
    void reportFeed_Success() {
        // given
        when(memberService.getMember(reporter.getId())).thenReturn(reporter);
        when(feedCommandService.getFeed(testFeed.getId())).thenReturn(testFeed);

        // when
        feedReportService.reportFeed(reporter.getId(), testFeed.getId(), ReportCategory.SPAM, false);

        // then
        FeedReport savedReport = feedReportRepository.findAll().get(0);
        assertThat(savedReport).isNotNull();
        assertThat(savedReport.getReporter().getId()).isEqualTo(reporter.getId());
        assertThat(savedReport.getReported().getId()).isEqualTo(reported.getId());
        assertThat(savedReport.getCategory()).isEqualTo(ReportCategory.SPAM);

        long eventCount = applicationEvents.stream(FeedReportedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }
}

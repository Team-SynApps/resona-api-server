
package com.synapps.resona.report.command.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.command.repository.FeedRepository;
import com.synapps.resona.report.command.entity.FeedReport;
import com.synapps.resona.report.command.repository.ReportRepository;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.common.entity.ReportStatus;
import com.synapps.resona.report.event.ReportStatusChangedEvent;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.support.ServiceLayerTest;
import com.synapps.resona.support.fixture.FeedFixture;
import fixture.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.ApplicationEvents;
import support.config.TestContainerConfig;
import support.database.DatabaseCleaner;

@ServiceLayerTest
@Import({ReportManagementService.class, DatabaseCleaner.class, TestContainerConfig.class})
class ReportManagementServiceTest {

    @Autowired
    private ReportManagementService reportManagementService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ApplicationEvents applicationEvents;

    private Member reporter;
    private Member reported;
    private Feed testFeed;
    private FeedReport testReport;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
        reporter = MemberFixture.createProfileTestMember("reporter@gmail.com");
        reported = MemberFixture.createProfileTestMember("reported@gmail.com");
        memberRepository.saveAll(java.util.List.of(reporter, reported));

        testFeed = FeedFixture.createDeletableFeed(reported);
        feedRepository.save(testFeed);

        testReport = FeedReport.of(reporter, reported, ReportCategory.SPAM, testFeed);
        reportRepository.save(testReport);
    }

    @Test
    @DisplayName("신고를 성공적으로 해결 처리한다.")
    void resolveReport_Success() {
        // when
        reportManagementService.resolveReport(testReport.getId());

        // then
        assertThat(testReport.getReportStatus()).isEqualTo(ReportStatus.RESOLVED);

        long eventCount = applicationEvents.stream(ReportStatusChangedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }

    @Test
    @DisplayName("신고를 성공적으로 거절 처리한다.")
    void rejectReport_Success() {
        // when
        reportManagementService.rejectReport(testReport.getId());

        // then
        assertThat(testReport.getReportStatus()).isEqualTo(ReportStatus.REJECTED);

        long eventCount = applicationEvents.stream(ReportStatusChangedEvent.class).count();
        assertThat(eventCount).isEqualTo(1);
    }
}

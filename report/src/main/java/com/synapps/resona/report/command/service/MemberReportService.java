package com.synapps.resona.report.command.service;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.command.service.MemberService;
import com.synapps.resona.query.event.MemberBlockedEvent;
import com.synapps.resona.report.command.entity.MemberReport;
import com.synapps.resona.report.command.repository.MemberReportRepository;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.dto.response.MemberReportResponse;
import com.synapps.resona.report.event.MemberReportedEvent;
import com.synapps.resona.report.exception.ReportException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberReportService {

    private final MemberReportRepository memberReportRepository;
    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public MemberReportResponse reportMember(Long reporterId, Long reportedId, ReportCategory category, boolean isBlocked) {
        Member reporter = memberService.getMember(reporterId);
        Member reported = memberService.getMember(reportedId);

        if (memberReportRepository.existsByReporterAndReported(reporter, reported)) {
            throw ReportException.alreadyReported();
        }

        MemberReport memberReport = MemberReport.of(reporter, reported, category);
        memberReportRepository.save(memberReport);

        MemberReportedEvent reportedEvent = new MemberReportedEvent(
                memberReport.getId(),
                reporter.getId(),
                reporter.getProfile().getNickname(),
                reported.getId(),
                reported.getProfile().getNickname(),
                category,
                memberReport.getCreatedAt()
        );
        eventPublisher.publishEvent(reportedEvent);

        if (isBlocked) {
            eventPublisher.publishEvent(new MemberBlockedEvent(reporterId, reported.getId()));
        }

        return new MemberReportResponse(memberReport);
    }
}

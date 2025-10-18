package com.synapps.resona.report.command.service;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.comment.command.repository.reply.ReplyRepository;
import com.synapps.resona.comment.exception.ReplyException;
import com.synapps.resona.query.event.MemberBlockedEvent;
import com.synapps.resona.report.command.entity.ReplyReport;
import com.synapps.resona.report.command.repository.ReplyReportRepository;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.dto.response.ReplyReportResponse;
import com.synapps.resona.report.event.ReplyReportedEvent;
import com.synapps.resona.report.exception.ReportException;
import com.synapps.resona.command.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyReportService {

    private final ReplyReportRepository replyReportRepository;
    private final MemberService memberService;
    private final ReplyRepository replyRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReplyReportResponse reportReply(Long reporterId, Long replyId, ReportCategory category, boolean isBlocked) {
        Member reporter = memberService.getMember(reporterId);
        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyException::replyNotFound);
        Member reported = reply.getMember();

        if (replyReportRepository.existsByReporterAndReplyId(reporter, replyId)) {
            throw ReportException.alreadyReported();
        }

        ReplyReport replyReport = ReplyReport.of(reporter, reported, replyId, category);
        replyReportRepository.save(replyReport);

        ReplyReportedEvent reportedEvent = new ReplyReportedEvent(
            replyReport.getId(),
            reporter.getId(),
            reporter.getProfile().getNickname(),
            reported.getId(),
            reported.getProfile().getNickname(),
            replyId,
            "", // Content is not available anymore
            category,
            replyReport.getCreatedAt()
        );
        eventPublisher.publishEvent(reportedEvent);

        if (isBlocked) {
            eventPublisher.publishEvent(new MemberBlockedEvent(reporterId, reported.getId()));
        }

        return new ReplyReportResponse(replyReport);
    }
}

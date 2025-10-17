package com.synapps.resona.report.command.service;

import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.comment.command.repository.comment.CommentRepository;
import com.synapps.resona.comment.exception.CommentException;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.query.event.MemberBlockedEvent;
import com.synapps.resona.report.command.entity.CommentReport;
import com.synapps.resona.report.command.repository.CommentReportRepository;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.event.CommentReportedEvent;
import com.synapps.resona.report.exception.ReportException;
import com.synapps.resona.command.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentReportService {

    private final CommentReportRepository commentReportRepository;
    private final MemberService memberService;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void reportComment(Long reporterId, Long commentId, ReportCategory category, boolean isBlocked) {
        Member reporter = memberService.getMember(reporterId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentException::commentNotFound);
        Member reported = comment.getMember();

        if (commentReportRepository.existsByReporterAndComment(reporter, comment)) {
            throw ReportException.alreadyReported();
        }

        CommentReport commentReport = CommentReport.of(reporter, reported, comment, category);
        commentReportRepository.save(commentReport);

        CommentReportedEvent reportedEvent = new CommentReportedEvent(
            commentReport.getId(),
            reporter.getId(),
            reporter.getProfile().getNickname(),
            reported.getId(),
            reported.getProfile().getNickname(),
            commentId,
            comment.getContent().substring(0, Math.min(comment.getContent().length(), 100)),
            category,
            commentReport.getCreatedAt()
        );
        eventPublisher.publishEvent(reportedEvent);

        if (isBlocked) {
            eventPublisher.publishEvent(new MemberBlockedEvent(reporterId, reported.getId()));
        }
    }
}

package com.synapps.resona.report.command.service;

import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.report.dto.request.UnifiedReportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnifiedReportService {

    private final FeedReportService feedReportService;
    private final CommentReportService commentReportService;
    private final ReplyReportService replyReportService;
    private final MemberReportService memberReportService;

    @Transactional
    public Object report(Member reporter, UnifiedReportRequest request) {
        return switch (request.getReportType()) {
            case FEED -> feedReportService.reportFeed(
                    reporter.getId(),
                    request.getTargetId(),
                    request.getCategory(),
                    request.isBlocked()
            );
            case COMMENT -> commentReportService.reportComment(
                    reporter.getId(),
                    request.getTargetId(),
                    request.getCategory(),
                    request.isBlocked()
            );
            case REPLY -> replyReportService.reportReply(
                    reporter.getId(),
                    request.getTargetId(),
                    request.getCategory(),
                    request.isBlocked()
            );
            case MEMBER -> memberReportService.reportMember(
                    reporter.getId(),
                    request.getTargetId(),
                    request.getCategory(),
                    request.isBlocked()
            );
        };
    }
}

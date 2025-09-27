package com.synapps.resona.query.service;

import com.synapps.resona.domain.entity.comment.Comment;
import com.synapps.resona.domain.entity.comment.Reply;
import com.synapps.resona.exception.CommentException;
import com.synapps.resona.exception.ReplyException;
import com.synapps.resona.domain.repository.comment.comment.CommentRepository;
import com.synapps.resona.domain.repository.comment.reply.ReplyRepository;
import com.synapps.resona.dto.MemberDto;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.event.ReportCreatedEvent;
import com.synapps.resona.exception.MemberException;
import com.synapps.resona.domain.entity.feed.Feed;
import com.synapps.resona.exception.FeedException;
import com.synapps.resona.domain.repository.feed.FeedRepository;
import com.synapps.resona.query.dto.report.request.CommentReportRequest;
import com.synapps.resona.query.dto.report.request.FeedReportRequest;
import com.synapps.resona.query.dto.report.request.ReplyReportRequest;
import com.synapps.resona.domain.entity.report.CommentReport;
import com.synapps.resona.domain.entity.report.FeedReport;
import com.synapps.resona.domain.entity.report.ReplyReport;
import com.synapps.resona.domain.repository.report.ReportRepository;
import com.synapps.resona.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;
  private final MemberRepository memberRepository;
  private final FeedRepository feedRepository;
  private final CommentRepository commentRepository;
  private final ReplyRepository replyRepository;
  
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void reportFeed(FeedReportRequest request, MemberDto memberInfo) {
    Member reporter = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Member reported = memberRepository.findById(request.getReportedId())
        .orElseThrow(MemberException::memberNotFound);

    Feed feed = feedRepository.findById(request.getFeedId())
        .orElseThrow(FeedException::feedNotFoundException);

    FeedReport feedReport = FeedReport.of(reporter, reported, request.getReportCategory(), feed);
    reportRepository.save(feedReport);
    eventPublisher.publishEvent(new ReportCreatedEvent(
        feedReport.getId(),
        "FEED",
        reporter.getId(),
        reported.getId(),
        feed.getId(),
        request.getReportCategory().getDescription()
    ));
  }

  @Transactional
  public void reportComment(CommentReportRequest request, MemberDto memberInfo) {
    Member reporter = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Member reported = memberRepository.findById(request.getReportedId())
        .orElseThrow(MemberException::memberNotFound);

    Comment comment = commentRepository.findById(request.getCommentId())
        .orElseThrow(CommentException::commentNotFound);

    CommentReport commentReport = CommentReport.of(reporter, reported, comment,
        request.getReportCategory());
    reportRepository.save(commentReport);
    eventPublisher.publishEvent(new ReportCreatedEvent(
        commentReport.getId(),
        "COMMENT",
        reporter.getId(),
        reported.getId(),
        comment.getId(),
        request.getReportCategory().getDescription()
    ));
  }

  @Transactional
  public void reportReply(ReplyReportRequest request, MemberDto memberInfo) {
    Member reporter = memberRepository.findByEmail(memberInfo.getEmail())
        .orElseThrow(MemberException::memberNotFound);

    Member reported = memberRepository.findById(request.getReportedId())
        .orElseThrow(MemberException::memberNotFound);

    Reply reply = replyRepository.findById(request.getReplyId())
        .orElseThrow(ReplyException::replyNotFound);

    ReplyReport replyReport = ReplyReport.of(reporter, reported, reply,
        request.getReportCategory());
    reportRepository.save(replyReport);
    eventPublisher.publishEvent(new ReportCreatedEvent(
        replyReport.getId(),
        "REPLY",
        reporter.getId(),
        reported.getId(),
        reply.getId(),
        request.getReportCategory().getDescription()
    ));
  }
}